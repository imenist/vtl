package com.vitalo.markrun.data.remote.model

import com.google.gson.annotations.SerializedName

data class NewStoreAdModuleBody(
    @SerializedName("device")
    val device: NewStoreDeviceInfo? = null,
    @SerializedName("virtual_id")
    val virtualId: Int,
    @SerializedName("keywords")
    val keywords: List<String>? = null
)
