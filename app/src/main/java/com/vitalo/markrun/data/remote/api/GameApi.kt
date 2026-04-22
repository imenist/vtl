package com.vitalo.markrun.data.remote.api

import com.vitalo.markrun.data.remote.model.*
import retrofit2.http.Body
import retrofit2.http.POST

interface GameApi {
    @POST("/ISO1880520")
    suspend fun getWithdrawalConfig(@Body payload: @JvmSuppressWildcards Map<String, Any>): CommonResponse<WithdrawalConfig>

    @POST("/ISO1880617")
    suspend fun withdraw(@Body payload: @JvmSuppressWildcards Map<String, Any>): CommonResponse<WithdrawalResult>

    @POST("/ISO1880615")
    suspend fun getWithdrawalRecords(@Body payload: @JvmSuppressWildcards Map<String, Any>): CommonResponse<WithdrawalRecords>

    @POST("/ISO1880619")
    suspend fun queryWithdrawalStatus(@Body payload: @JvmSuppressWildcards Map<String, Any>): CommonResponse<MerchantInfos>

    @POST("/ISO1880611")
    suspend fun getGameConfig(@Body payload: @JvmSuppressWildcards Map<String, Any>): CommonResponse<EmptyData>
}
