package com.vitalo.markrun.data.remote.model

import com.google.gson.annotations.SerializedName

data class AdModuleResponse(
    @SerializedName("p_module")
    val pModule: AdModuleItem? = null,
    @SerializedName("c_module")
    val cModule: List<AdModuleItem>? = null
)

data class AdModuleItem(
    @SerializedName("module_id")
    val moduleId: Int? = null,
    @SerializedName("name")
    val name: String? = null,
    @SerializedName("ad_id")
    val adId: String? = null,
    @SerializedName("ad_source")
    val adSource: Int? = null,
    @SerializedName("ad_type")
    val adType: Int? = null,
    @SerializedName("sort")
    val sort: Int? = null,
    @SerializedName("single_request_overtime")
    val singleRequestOvertime: Int? = null,
    @SerializedName("flow_request_overtime")
    val flowRequestOvertime: Int? = null,
    @SerializedName("daily_display_limit")
    val dailyDisplayLimit: Int? = null,
    @SerializedName("custom_field")
    val customField: String? = null,
    @SerializedName("custom_field2")
    val customField2: String? = null,
    @SerializedName("custom_field3")
    val customField3: String? = null,
    @SerializedName("custom_field4")
    val customField4: String? = null,
    @SerializedName("custom_field5")
    val customField5: String? = null
) {
    @Transient
    var virtualId: Int? = null
}
