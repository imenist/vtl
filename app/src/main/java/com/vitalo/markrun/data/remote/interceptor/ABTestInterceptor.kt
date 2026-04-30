package com.vitalo.markrun.data.remote.interceptor

import com.vitalo.markrun.data.remote.crypto.DESEncryptor
import com.vitalo.markrun.data.remote.crypto.HMACSigner
import com.vitalo.markrun.util.Constants
import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody

/**
 * ABTest 域拦截器。
 * 差异：
 * - GET 请求，不加密 payload
 * - HMAC 密钥与旧 AbtestCenterService.Builder.accessKey(...) 一致：使用 [AppConfig.abTestSecretKey]
 * - DES Key 用 UTF-8（isKeyEncoded=false）
 * - 响应始终解密（URL-safe Base64 → DES 解密）
 * - 额外 Header: Server-Encrypt, timestamp, isABTestCenter
 */
class ABTestInterceptor : Interceptor {
    private val hmacSecretKey get() = Constants.ABTEST_SECRET_KEY
    private val desKey get() = DESEncryptor.decodeKeyIfNeed(Constants.ABTEST_SECRET_KEY, isKeyEncoded = false)

    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val method = original.method.uppercase()
        val path = original.url.encodedPath

        // 与 ReelCoin/旧签名实现一致：直接使用 URL 的 encodedQuery（保持参数顺序与转义）
        val queryString = original.url.encodedQuery.orEmpty()
        val signString = "$method\n$path\n$queryString\n"
        val signature = HMACSigner.hmacSHA256(signString, hmacSecretKey)

        val newRequest = original.newBuilder()
            .header("X-Signature", signature)
            .header("Server-Encrypt", "true")
            .header("timestamp", System.currentTimeMillis().toString())
            .header("isABTestCenter", "true")
            .build()

        val response = chain.proceed(newRequest)

        val responseBody = response.body?.string() ?: ""
        return if (responseBody.isNotEmpty()) {
            try {
                val decryptedBytes = DESEncryptor.decryptFromUrlSafeBase64(responseBody, desKey)
                response.newBuilder()
                    .body(String(decryptedBytes, Charsets.UTF_8).toResponseBody(response.body?.contentType()))
                    .build()
            } catch (_: Exception) {
                response.newBuilder()
                    .body(responseBody.toResponseBody(response.body?.contentType()))
                    .build()
            }
        } else {
            response
        }
    }
}
