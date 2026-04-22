package com.vitalo.markrun.data.remote.model

import com.google.gson.annotations.SerializedName

data class Token(
    @SerializedName("access_token") val accessToken: String?,
    @SerializedName("expired_in") val expiredIn: Int,
    @SerializedName("refresh_token") val refreshToken: String?
)

data class TokenWrapper(
    val token: Token
)
