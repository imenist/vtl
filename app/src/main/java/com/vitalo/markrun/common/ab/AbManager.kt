package com.vitalo.markrun.common.ab

import android.content.Context
import android.util.Log
import com.google.gson.GsonBuilder
import com.vitalo.markrun.common.ab.repo.SimpleDataRepo
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * AB 业务管理器（核心入口）。
 *
 * 职责：
 * 1. 管理 [AbConfigContract] 注册列表（即 [AbSidTable] 中定义的实验）
 * 2. 统一委托 [AbConfigDataRepo] 执行远端拉取（含 8 小时缓存策略）
 * 3. 本类仅负责把磁盘 JSON 解析为 `markrun.ab` 的强类型对象，兼容旧调用方
 * 4. 将解析后的 [AbConfigResponse] 暴露为 [StateFlow]，供 ViewModel 观察
 *
 * 使用方式：
 * ```kotlin
 * // 1. 在 Application 启动时触发（已在 VitaloApp 中调用）
 * abManager.refreshIfNeeded()
 *
 * // 2. 读取某个实验配置
 * val adPolicy = abManager.getConfig(AbSidTable.AD_POLICY) as? AdPolicyConfig
 * ```
 */
@Singleton
class AbManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val TAG = "AbManager"

    // ── 注册的实验列表 ────────────────────────────────────────────────────────
    private val contracts = mutableListOf<AbConfigContract>().apply {
        add(AbSidTable.KCAL_LIMIT)
        add(AbSidTable.NEW_USER_SPIN)
        add(AbSidTable.SIGN_REWARD)
        add(AbSidTable.HAMMER)
        add(AbSidTable.SLOT_MACHINE)
        add(AbSidTable.FLOP_COIN)
        add(AbSidTable.TASK_REWARD)
        add(AbSidTable.MINI_GAME_SWITCH)
        add(AbSidTable.DAILY_GUIDE)
        add(AbSidTable.AD)
        add(AbSidTable.AD_POLICY)
        add(AbSidTable.WITHDRAW_ENABLE)
        add(AbSidTable.WITHDRAW_GRADE)
        add(AbSidTable.H5_TASK_AD)
        add(AbSidTable.APP_UI_SWITCH)
    }

    // ── 状态暴露 ──────────────────────────────────────────────────────────────
    private val _response = MutableStateFlow<AbConfigResponse>(AbConfigResponse())
    val response: StateFlow<AbConfigResponse> = _response.asStateFlow()

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val jsonCacheFile by lazy {
        val dir = java.io.File(context.cacheDir, "ab_data").also { it.mkdirs() }
        java.io.File(dir, "ab_request_data_cache.json")
    }

    // ── 公共 API ──────────────────────────────────────────────────────────────

    /**
     * 启动时调用：先加载本地缓存，再交给 [AbConfigDataRepo] 决定是否请求远端。
     */
    fun init() {
        scope.launch {
            loadCache()
            refreshIfNeeded()
        }
    }

    /**
     * 强制刷新（忽略缓存策略），底层统一走 [AbConfigDataRepo]。
     */
    fun forceRefresh() {
        scope.launch {
            AbConfigDataRepo.refreshRemoteData(true)
            awaitRepoResultAndReload()
        }
    }

    /**
     * 挂起直到本次远端拉取结束（用于调试面板等需等待结果的场景）。
     */
    suspend fun awaitForceRefresh() {
        withContext(Dispatchers.IO) {
            AbConfigDataRepo.refreshRemoteData(true)
            awaitRepoResultAndReload()
        }
    }

    /**
     * 将当前内存中已解析的全部 AB 实验格式化为可读文本（按 SID 分组，每条 cfg 为 JSON）。
     */
    fun formatDebugSummary(): String {
        val resp = _response.value
        val datas = resp.datas
        if (datas.isNullOrEmpty()) {
            return "暂无 AB 数据（可能尚未拉取成功、解析失败或磁盘无缓存）。"
        }
        val gson = GsonBuilder().setPrettyPrinting().serializeNulls().create()
        return buildString {
            appendLine("以下为 AbManager（markrun.ab）当前内存中的解析结果。")
            appendLine()
            contracts.forEach { contract ->
                val key = AbResultParser.getSidKey(contract.sid)
                val data = datas[key]
                appendLine("════════ $key  (${contract.type.simpleName}) ════════")
                if (data == null) {
                    appendLine("(响应中无此 key)")
                    appendLine()
                    return@forEach
                }
                appendLine("filter_id=${data.filterId}, abtest_id=${data.abtestId}")
                val list = data.cfgs.orEmpty()
                if (list.isEmpty()) {
                    appendLine("  (cfgs 为空)")
                } else {
                    list.forEachIndexed { i, cfg ->
                        appendLine("  --- cfg[$i] ${cfg.javaClass.simpleName} cfg_id=${cfg.cfgId} ---")
                        appendLine(gson.toJson(cfg))
                    }
                }
                appendLine()
            }
        }
    }

    /**
     * 按需刷新：缓存未过期时跳过网络请求。
     */
    fun refreshIfNeeded() {
        scope.launch {
            AbConfigDataRepo.refreshRemoteData(false)
            awaitRepoResultAndReload()
        }
    }

    /**
     * 获取指定实验的第一条配置（最常用的快捷方式）。
     */
    fun getConfig(contract: AbConfigContract): BaseAbConfig? {
        val key = AbResultParser.getSidKey(contract.sid)
        return _response.value.datas?.get(key)?.cfgs?.firstOrNull()
    }

    /**
     * 获取指定实验的全部配置列表。
     */
    fun getAllConfigs(contract: AbConfigContract): List<BaseAbConfig>? {
        val key = AbResultParser.getSidKey(contract.sid)
        return _response.value.datas?.get(key)?.cfgs
    }

    // ── 内部实现 ──────────────────────────────────────────────────────────────

    private suspend fun awaitRepoResultAndReload(timeoutMs: Long = 15_000L) {
        val start = System.currentTimeMillis()
        while (System.currentTimeMillis() - start < timeoutMs) {
            val status = AbConfigDataRepo.getStatusLiveData().value
            if (status == SimpleDataRepo.FetchStatus.SUCCESS || status == SimpleDataRepo.FetchStatus.FAILED) {
                break
            }
            delay(200)
        }
        loadCache()
    }

    private fun loadCache() {
        try {
            if (!jsonCacheFile.exists()) return
            val json = jsonCacheFile.readText().takeIf { it.isNotBlank() } ?: return
            val parsed = AbResultParser.extract(json, contracts)
            if (parsed != null) {
                _response.value = parsed
                Log.d(TAG, "AB 磁盘缓存加载成功")
            }
        } catch (e: Exception) {
            Log.w(TAG, "AB 磁盘缓存加载失败: ${e.message}")
        }
    }
}
