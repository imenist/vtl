package com.vitalo.markrun.common.http.bean

import com.google.gson.annotations.SerializedName

class ApiResponse<T>(
    @SerializedName("data")
    var data: T?,
    @SerializedName("error_code")
    var errorCode: Int,
    @SerializedName("error_message")
    var errorMsg: String? = null
)
