package com.vitalo.markrun.data.remote.model

import com.google.gson.annotations.SerializedName

data class CommonResponse<T>(
    @SerializedName("error_code") val errorCode: Int,
    @SerializedName("error_message") val errorMessage: String?,
    val data: T?
) {
    val isSuccess: Boolean get() = errorCode == 0
}

class EmptyData
