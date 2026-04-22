package com.vitalo.markrun.data.remote.interceptor

import android.util.Log
import com.vitalo.markrun.data.remote.crypto.DESEncryptor
import com.vitalo.markrun.data.remote.crypto.HMACSigner
import com.vitalo.markrun.util.Constants
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import okio.Buffer

class NewStoreInterceptor : Interceptor {
    private val apiKey get() = Constants.AD_API_KEY
    private val apiSecret get() = Constants.AD_SECRET_KEY
    private val desKey get() = DESEncryptor.decodeKeyIfNeed(Constants.AD_DES_KEY, isKeyEncoded = true)

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
        } ?: ""

        val builtUrl = urlBuilder.build()
        val queryString = builtUrl.encodedQuery ?: ""

        val signString = "$method\n$path\n$queryString\n$rawPayloadJson"
        val signature = HMACSigner.hmacSHA256(signString, apiSecret)

        Log.d(TAG, "URL: $builtUrl")
        Log.d(TAG, "SignString: $signString")
        Log.d(TAG, "Signature: $signature")

        val encryptedBody = if (rawPayloadJson.isNotEmpty()) {
            DESEncryptor.encryptToUrlSafeBase64(
                rawPayloadJson.toByteArray(Charsets.UTF_8), desKey
            )
        } else ""

        val newRequest = original.newBuilder()
            .url(builtUrl)
            .header("X-Signature", signature)
            .header("Content-Type", "application/json;charset=UTF-8")
            .header("X-Crypto", "des")
            .method(
                method,
                if (method != "GET") encryptedBody.toRequestBody("application/json".toMediaType())
                else null
            )
            .build()

        val response = chain.proceed(newRequest)
        Log.d(TAG, "Response code: ${response.code}")

        if (!response.isSuccessful) return response

        val responseBody = response.body ?: return response
        val bytes = responseBody.bytes()
        if (bytes.isEmpty()) {
            return response.newBuilder()
                .body(bytes.toResponseBody(responseBody.contentType()))
                .build()
        }

        return try {
            val decrypted = DESEncryptor.decrypt(bytes, desKey)
            val decryptedStr = String(decrypted, Charsets.UTF_8)
            Log.d(TAG, "Decrypted response: ${decryptedStr.take(200)}")
            response.newBuilder()
                .body(decryptedStr.toResponseBody(responseBody.contentType()))
                .build()
        } catch (e: Exception) {
            Log.e(TAG, "Decryption failed, returning raw response: ${e.message}")
            response.newBuilder()
                .body(bytes.toResponseBody(responseBody.contentType()))
                .build()
        }
    }

    companion object {
        private const val TAG = "NewStoreInterceptor"
    }
}
