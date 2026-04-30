package com.vitalo.markrun.common.ab

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import com.vitalo.markrun.common.ab.impl.*

@Keep
class AbConfigResponse {

    @SerializedName("success")
    var success: Boolean? = null

    @SerializedName("datas")
    var datas: Map<String, Data>? = null

    @SerializedName("message")
    var message: String? = null

    @SerializedName("status")
    var status: Int? = null

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