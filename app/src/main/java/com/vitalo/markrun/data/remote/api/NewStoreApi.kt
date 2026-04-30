package com.vitalo.markrun.data.remote.api

import com.vitalo.markrun.data.remote.model.CommonResponse
import com.vitalo.markrun.data.remote.model.AdModuleResponse
import com.vitalo.markrun.data.remote.model.NewStoreAdModuleBody
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface NewStoreApi {
    @Headers(com.vitalo.markrun.common.http.host.BaseUrlHeader.BASE_URL_AD_HEADER)
    @POST("/ISO1801101")
    suspend fun getAdModule(@Body body: NewStoreAdModuleBody): CommonResponse<AdModuleResponse>
}
