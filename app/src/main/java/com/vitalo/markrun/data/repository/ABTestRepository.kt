package com.vitalo.markrun.data.repository

import com.vitalo.markrun.ab.AbConfigContract
import com.vitalo.markrun.ab.AbManager
import com.vitalo.markrun.ab.AbConfigResponse
import com.vitalo.markrun.common.ab.BaseAbConfig
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * AB 数据仓库层。
 * 包装 [AbManager]，供上层 ViewModel 注入使用。
 * ViewModel 通过 [response] 观察 AB 配置变化，或通过 [getConfig] 取单条配置。
 */
@Singleton
class ABTestRepository @Inject constructor(
    private val abManager: AbManager
) {
    /** 观察整体 AB 响应（StateFlow） */
    val response: StateFlow<AbConfigResponse?> = abManager.response

    /** 获取指定实验的第一条配置 */
    fun getConfig(contract: AbConfigContract): BaseAbConfig? =
        abManager.getConfig(contract)

    /** 获取指定实验的全部配置列表 */
    fun getAllConfigs(contract: AbConfigContract): List<BaseAbConfig>? =
        abManager.getAllConfigs(contract)

    /** 强制刷新（忽略缓存） */
    fun forceRefresh() = abManager.forceRefresh()
}

