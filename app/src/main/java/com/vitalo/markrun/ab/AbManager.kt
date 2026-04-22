package com.vitalo.markrun.ab

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.vitalo.markrun.config.AppConfig
import com.vitalo.markrun.data.remote.api.ABTestApi
import com.vitalo.markrun.data.local.prefs.AppPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Locale
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * AB 业务管理器（核心入口）。
 *
 * 职责：
 * 1. 管理 [AbConfigContract] 注册列表（即 [AbSidTable] 中定义的实验）
 * 2. 通过 Retrofit [ABTestApi] 拉取远端配置，并用 [AbResultParser] 解析
 * 3. 8 小时缓存策略（SharedPreferences 存时间戳 + 文件存 JSON）
 * 4. 将解析后的 [AbConfigResponse] 暴露为 [StateFlow]，供 ViewModel 观察
 *
 * 使用方式：
 * ```kotlin
 * // 1. 在 Application 启动时触发（已在 VitaloApp 中调用）
 * abManager.refreshIfNeeded()
 *
 * // 2. 读取某个实验配置
 * val cfg = abManager.getConfig(AbSidTable.COMMON) as? CommonAbConfig
 * ```
 */
@Singleton
class AbManager @Inject constructor(
    private val api: ABTestApi,
    private val appPreferences: AppPreferences,
    @ApplicationContext private val context: Context
) {
    private val TAG = "AbManager"

    // ── 注册的实验列表 ────────────────────────────────────────────────────────
    private val contracts = mutableListOf<AbConfigContract>().apply {
        // TODO: 添加更多实验时，在此 add(AbSidTable.XXX)
        add(AbSidTable.COMMON)
    }

    // ── 状态暴露 ──────────────────────────────────────────────────────────────
    private val _response = MutableStateFlow<AbConfigResponse?>(null)
    val response: StateFlow<AbConfigResponse?> = _response.asStateFlow()

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    // ── SharedPreferences（缓存时间戳） ───────────────────────────────────────
    private val cachePrefs: SharedPreferences by lazy {
        context.getSharedPreferences("ab_cache_prefs", Context.MODE_PRIVATE)
    }

    private val jsonCacheFile by lazy {
        val dir = java.io.File(context.cacheDir, "ab_data").also { it.mkdirs() }
        java.io.File(dir, "ab_request_data_cache.json")
    }

    // ── 公共 API ──────────────────────────────────────────────────────────────

    /**
     * 启动时调用：先从磁盘读取缓存，再决定是否发起网络请求。
     */
    fun init() {
        scope.launch {
            loadCache()
            refreshIfNeeded()
        }
    }

    /**
     * 强制刷新（忽略缓存时间策略）。
     */
    fun forceRefresh() {
        scope.launch { fetchRemote() }
    }

    /**
     * 按需刷新：缓存未过期时跳过网络请求。
     */
    fun refreshIfNeeded() {
        if (!isCacheExpired()) {
            Log.d(TAG, "AB 缓存有效，跳过请求")
            return
        }
        scope.launch { fetchRemote() }
    }

    /**
     * 获取指定实验的第一条配置（最常用的快捷方式）。
     */
    fun getConfig(contract: AbConfigContract): BaseAbConfig? {
        val key = AbResultParser.getSidKey(contract.sid)
        return _response.value?.datas?.get(key)?.cfgs?.firstOrNull()
    }

    /**
     * 获取指定实验的全部配置列表。
     */
    fun getAllConfigs(contract: AbConfigContract): List<BaseAbConfig>? {
        val key = AbResultParser.getSidKey(contract.sid)
        return _response.value?.datas?.get(key)?.cfgs
    }

    // ── 内部实现 ──────────────────────────────────────────────────────────────

    private fun buildSidArray(): String =
        contracts.joinToString(",") { it.sid.toString() }

    private suspend fun fetchRemote() {
        Log.d(TAG, "开始拉取 AB 配置，SIDs=${buildSidArray()}")
        try {
            val rawJson = api.getABTest(
                gzip      = "0",
                pkgName   = AppConfig.packageName,
                sid       = buildSidArray(),
                cid       = AppConfig.abTestCid,
                cversion  = AppConfig.versionCode.toString(),
                local     = Locale.getDefault().country,
                entrance  = AppConfig.abTestEntrance,
                cdays     = getInstallDays().toString(),
                aid       = appPreferences.getDistinctId(),
                userFrom  = "0",
                prodkey   = AppConfig.abTestProductKey
            )
            Log.d(TAG, "AB 原始响应长度: ${rawJson.length}")
            val parsed = AbResultParser.extract(rawJson, contracts)
            if (parsed != null) {
                Log.d(TAG, "AB 解析成功")
                _response.value = parsed
                saveCacheTime(System.currentTimeMillis())
                saveJsonCache(rawJson)
            } else {
                Log.w(TAG, "AB 解析失败，仍使用旧缓存")
            }
        } catch (e: Exception) {
            Log.e(TAG, "AB 请求异常: ${e.message}")
        }
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

    private fun saveJsonCache(json: String) {
        try {
            jsonCacheFile.writeText(json)
        } catch (e: Exception) {
            Log.w(TAG, "AB 缓存写入失败: ${e.message}")
        }
    }

    // ── 缓存时间策略（8 小时） ────────────────────────────────────────────────

    private fun getCacheTime(): Long =
        cachePrefs.getLong(PREF_KEY_CACHE_TIME, 0L)

    private fun saveCacheTime(time: Long) =
        cachePrefs.edit().putLong(PREF_KEY_CACHE_TIME, time).apply()

    private fun isCacheExpired(): Boolean {
        val diff = System.currentTimeMillis() - getCacheTime()
        return diff > TimeUnit.HOURS.toMillis(8)
    }

    // ── 工具方法 ──────────────────────────────────────────────────────────────

    /**
     * 计算安装天数（基于 AppPreferences 中存储的安装日期）。
     */
    private fun getInstallDays(): Int {
        val installDate = appPreferences.getLong(AppPreferences.KEY_INSTALL_DATE, 0L)
        if (installDate == 0L) return 0
        val diffMs = System.currentTimeMillis() - installDate
        return (diffMs / TimeUnit.DAYS.toMillis(1)).toInt().coerceAtLeast(0)
    }

    companion object {
        private const val PREF_KEY_CACHE_TIME = "ab_cache_time_ab_request_data"
    }
}
