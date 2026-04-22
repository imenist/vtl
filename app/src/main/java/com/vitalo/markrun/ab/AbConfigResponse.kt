package com.vitalo.markrun.ab

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

/**
 * AB 接口的顶层响应结构。
 *
 * 单 SID 时 key 固定为 "infos"；
 * 多 SID 时 key 为 "infos_<sid>"。
 */
@Keep
class AbConfigResponse {

    @SerializedName("error_code")
    var errorCode: Int? = null

    @SerializedName("error_message")
    var errorMessage: String? = null

    /**
     * key = "infos_<sid>"（多 SID）或 "infos"（单 SID）
     * value = 对应实验的数据
     */
    var datas: Map<String, Data>? = null

    @Keep
    class Data {
        @SerializedName("filter_id")
        var filterId: Int? = null

        @SerializedName("abtest_id")
        var abtestId: Int? = null

        @SerializedName("cfgs")
        var cfgs: List<BaseAbConfig>? = null
    }
}
