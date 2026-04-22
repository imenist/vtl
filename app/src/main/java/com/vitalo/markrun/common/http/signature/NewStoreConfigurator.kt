package com.vitalo.markrun.common.http.signature

import android.content.Context
import com.vitalo.markrun.config.AppConfig
import okhttp3.Request

class NewStoreConfigurator(context: Context) : BaseSignatureConfigurator(context) {
    override val apiKey: String
        get() = AppConfig.adApiKey
    override val apiSecret: String
        get() = AppConfig.adSecretKey

    override fun getNewRequest(request: Request): Request {
        val timestamp = System.currentTimeMillis()
        val newUrl = request.url.newBuilder()
            .addQueryParameter("api_key", apiKey)
            .addQueryParameter("timestamp", timestamp.toString())
            .build()

        val builder = request.newBuilder()
            .url(newUrl)
            .header(CONTENT_TYPE, "application/json") // 使用 header 而不是 addHeader 保证覆盖
            .header("X-Crypto", "des")

        builder.addHeader(SIGNATURE_KEY, getApiSignature(request.newBuilder().header(CONTENT_TYPE, "application/json").build(), newUrl))
        
        configRequestBuilder(builder)
        return builder.build()
    }

    override fun getNewResponse(response: okhttp3.Response): okhttp3.Response {
        return response.newBuilder().header("Server-Encrypt", "true").build()
    }
}