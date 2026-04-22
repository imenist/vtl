package com.vitalo.markrun.data.remote.interceptor

import com.vitalo.markrun.data.remote.crypto.DESEncryptor
import com.vitalo.markrun.data.remote.crypto.HMACSigner
import com.vitalo.markrun.util.Constants
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import okio.Buffer

/**
 * Coin/Game 域拦截器。
 * 复用 AccountCenter 的密钥体系，额外注入 X-Auth-Token。
 * 响应解密逻辑同 AccountCenter（原始 DES 密文 bytes）。
 */
class CoinInterceptor(
    private val tokenProvider: () -> String?,
    private val deviceInfoProvider: () -> Map<String, Any>
) : Interceptor {
    private val apiKey get() = Constants.ACCOUNT_API_KEY
    private val apiSecret get() = Constants.ACCOUNT_API_SECRET
    private val desKey get() = DESEncryptor.decodeKeyIfNeed(Constants.ACCOUNT_DES_KEY, isKeyEncoded = Constants.IS_ACCOUNT_DES_KEY_ENCODED)

    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val method = original.method.uppercase()
        val path = original.url.encodedPath

        val urlBuilder = original.url.newBuilder()
            .addQueryParameter("api_key", apiKey)
            .addQueryParameter("timestamp", System.currentTimeMillis().toString())

        val rawPayloadJson = original.body?.let { body ->
            val buffer = Buffer()
            body.writeTo(buffer)
            buffer.readUtf8()
        } ?: "{}"

        val jsonElement = com.google.gson.JsonParser.parseString(rawPayloadJson)
        val jsonObject = if (jsonElement.isJsonObject) jsonElement.asJsonObject else com.google.gson.JsonObject()
        jsonObject.add("device", com.google.gson.Gson().toJsonTree(deviceInfoProvider()))

        val injectedJsonBodyStr = com.google.gson.Gson().toJson(jsonObject)

        val sortedJson = TrainingInterceptor.sortJsonKeys(injectedJsonBodyStr)

        val encryptedBody = if (sortedJson.isNotEmpty()) {
            DESEncryptor.encryptToUrlSafeBase64(
                sortedJson.toByteArray(Charsets.UTF_8), desKey
            )
        } else ""

        val builtUrl = urlBuilder.build()
        val queryString = builtUrl.queryParameterNames.sorted().joinToString("&") { name ->
            "$name=${builtUrl.queryParameter(name)}"
        }
        // MUST use sortedJson for signature (matching iOS .sortedKeys behavior)
        val signString = "$method\n$path\n$queryString\n$sortedJson"
        val signature = HMACSigner.hmacSHA256(signString, apiSecret)

        val requestBuilder = original.newBuilder()
            .url(builtUrl)
            .header("X-Signature", signature)
            .header("Content-Type", "application/json;charset=UTF-8")
            .header("X-Crypto", "des")

        tokenProvider()?.let { token ->
            requestBuilder.header("X-Auth-Token", token)
        }

        val newRequest = requestBuilder
            .method(
                method,
                if (method != "GET") encryptedBody.toRequestBody("application/json".toMediaType())
                else null
            )
            .build()

        val response = chain.proceed(newRequest)
        val isEncrypted = response.headers.any {
            it.first.equals("X-Crypto", ignoreCase = true) && it.second.equals("des", ignoreCase = true)
        }

        return if (isEncrypted) {
            val responseBytes = response.body?.bytes() ?: byteArrayOf()
            val decryptedBytes = DESEncryptor.decrypt(responseBytes, desKey)
            response.newBuilder()
                .body(String(decryptedBytes, Charsets.UTF_8).toResponseBody(response.body?.contentType()))
                .build()
        } else {
            response
        }
    }
}
