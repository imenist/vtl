package com.vitalo.markrun.data.remote.api

import com.vitalo.markrun.data.remote.model.AutoLoginResult
import com.vitalo.markrun.data.remote.model.CommonResponse
import com.vitalo.markrun.data.remote.model.TokenWrapper
import retrofit2.http.Body
import retrofit2.http.POST

interface AccountApi {
    @POST("/ISO1880102")
    suspend fun autoLogin(@Body payload: @JvmSuppressWildcards Map<String, Any>): CommonResponse<AutoLoginResult>

    @POST("/ISO1880106")
    suspend fun refreshToken(@Body payload: @JvmSuppressWildcards Map<String, Any>): CommonResponse<TokenWrapper>
}
