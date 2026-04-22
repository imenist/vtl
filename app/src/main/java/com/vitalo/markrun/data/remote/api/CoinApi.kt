package com.vitalo.markrun.data.remote.api

import com.vitalo.markrun.data.remote.model.*
import retrofit2.http.Body
import retrofit2.http.POST

interface CoinApi {
    @POST("/ISO1880200")
    suspend fun getCoinTypes(@Body payload: @JvmSuppressWildcards Map<String, Any>): CommonResponse<CoinTypeList>

    @POST("/ISO1880211")
    suspend fun getCoinInfos(@Body payload: @JvmSuppressWildcards Map<String, Any>): CommonResponse<CoinInfoList>

    @POST("/ISO1880202")
    suspend fun orderOptCoin(@Body payload: @JvmSuppressWildcards Map<String, Any>): CommonResponse<String>

    @POST("/ISO1880203")
    suspend fun optCoin(@Body payload: @JvmSuppressWildcards Map<String, Any>): CommonResponse<EmptyData>

    @POST("/ISO1880216")
    suspend fun clearCoins(@Body payload: @JvmSuppressWildcards Map<String, Any>): CommonResponse<EmptyData>
}
