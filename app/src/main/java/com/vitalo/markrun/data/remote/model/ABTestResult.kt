package com.vitalo.markrun.data.remote.model

import com.google.gson.annotations.SerializedName

/**
 * AB 接口原始响应。
 * Retrofit 拿到后，[com.vitalo.markrun.ab.AbManager] 会把它序列化为 JSON
 * 再交给 [com.vitalo.markrun.ab.AbResultParser] 做多态解析，
 * 最终反映到 [com.vitalo.markrun.ab.AbConfigResponse] 中。
 *
 * 这里用宽松的 Map 保留所有字段，避免提前解析丢失信息。
 */
data class ABTestResult(
    @SerializedName("error_code")
    val errorCode: Int? = null,
    @SerializedName("error_message")
    val errorMessage: String? = null,
    // 单 SID 时含 "infos"，多 SID 时含 "infos_<sid>" —— 全部用 Map 承载
    @SerializedName("infos")
    val infos: Map<String, Any>? = null,
    // 支持多 SID 时服务端直接把 infos_xxx 放在顶层
    @SerializedName("datas")
    val datas: Map<String, Any>? = null
)
