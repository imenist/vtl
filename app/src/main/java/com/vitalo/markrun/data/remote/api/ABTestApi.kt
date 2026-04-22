package com.vitalo.markrun.data.remote.api

import retrofit2.http.GET
import retrofit2.http.Query

/**
 * AB 测试接口。
 * 返回原始 JSON 字符串，由 [com.vitalo.markrun.ab.AbManager] 使用
 * [com.vitalo.markrun.ab.AbResultParser] 做多态解析。
 * （需要 Retrofit 配置 ScalarsConverterFactory，NetworkModule 已配置）
 */
interface ABTestApi {
    @GET("/ISO1850001")
    suspend fun getABTest(
        @Query("gzip")       gzip: String,
        @Query("pkgname")    pkgName: String,
        @Query("sid")        sid: String,
        @Query("cid")        cid: String,
        @Query("cversion")   cversion: String,
        @Query("local")      local: String,
        @Query("entrance")   entrance: Int,
        @Query("cdays")      cdays: String,
        @Query("aid")        aid: String,
        @Query("user_from")  userFrom: String,
        @Query("prodkey")    prodkey: String
    ): String
}

