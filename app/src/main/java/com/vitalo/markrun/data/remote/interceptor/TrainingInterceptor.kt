package com.vitalo.markrun.data.remote.interceptor

import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.vitalo.markrun.data.remote.crypto.DESEncryptor
import com.vitalo.markrun.data.remote.crypto.HMACSigner
import com.vitalo.markrun.util.Constants
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import okio.Buffer

class TrainingInterceptor : Interceptor {
    private val apiKey get() = Constants.TRAINING_API_KEY
    private val apiSecret get() = Constants.TRAINING_API_SECRET
    private val desKey get() = DESEncryptor.decodeKeyIfNeed(Constants.TRAINING_DES_KEY, isKeyEncoded = true)

    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val method = original.method.uppercase()
        val path = original.url.encodedPath

        // 1. Query: inject api_key + timestamp
        val urlBuilder = original.url.newBuilder()
            .addQueryParameter("api_key", apiKey)
            .addQueryParameter("timestamp", System.currentTimeMillis().toString())

        // 2. Read raw payload for signing
        val rawPayloadJson = original.body?.let { body ->
            val buffer = Buffer()
            body.writeTo(buffer)
            buffer.readUtf8()
        } ?: ""

        // 3. Sort JSON keys (iOS uses .sortedKeys for both signature and encryption)
        val sortedJson = if (rawPayloadJson.isNotEmpty()) sortJsonKeys(rawPayloadJson) else ""

        // 4. Encrypt payload → URL-safe Base64
        val encryptedBody = if (sortedJson.isNotEmpty()) {
            DESEncryptor.encryptToUrlSafeBase64(
                sortedJson.toByteArray(Charsets.UTF_8), desKey
            )
        } else ""

        // 5. Signature: METHOD\npath\nqueryString\npayloadJson
        //    MUST use sortedJson (same as iOS TrainingSignatureProvider uses .sortedKeys)
        val builtUrl = urlBuilder.build()
        val queryString = builtUrl.query ?: ""
        val signString = "$method\n$path\n$queryString\n$sortedJson"
        val signature = HMACSigner.hmacSHA256(signString, apiSecret)

        Log.d("TrainingInterceptor", "URL: ${builtUrl}")
        Log.d("TrainingInterceptor", "SignString: $signString")
        Log.d("TrainingInterceptor", "Signature: $signature")

        // 6. Build new request
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

        // 7. Response decryption
        val response = chain.proceed(newRequest)
        Log.d("TrainingInterceptor", "Response code: ${response.code}")
        val isEncrypted = response.header("X-Encrypted")?.lowercase() == "true"

        return if (isEncrypted) {
            val responseBody = response.body?.string() ?: ""
            try {
                val decryptedBytes = DESEncryptor.decryptFromUrlSafeBase64(responseBody, desKey)
                val decryptedStr = String(decryptedBytes, Charsets.UTF_8)
                Log.d("TrainingInterceptor", "Decrypted response: ${decryptedStr.take(200)}")
                response.newBuilder()
                    .body(decryptedStr.toResponseBody(response.body?.contentType()))
                    .build()
            } catch (e: Exception) {
                Log.e("TrainingInterceptor", "Decryption failed: ${e.message}")
                Log.e("TrainingInterceptor", "Raw response: ${responseBody.take(200)}")
                response.newBuilder()
                    .body(responseBody.toResponseBody(response.body?.contentType()))
                    .build()
            }
        } else {
            val responseBody = response.peekBody(10240).string()
            Log.d("TrainingInterceptor", "Unencrypted response: ${responseBody.take(200)}")
            response
        }
    }

    companion object {
        fun sortJsonKeys(json: String): String {
            return try {
                val element = JsonParser.parseString(json)
                val sorted = sortJsonElement(element)
                Gson().toJson(sorted)
            } catch (_: Exception) {
                json
            }
        }

        /**
         * Recursively sort JSON keys at all levels,
         * matching iOS JSONSerialization's .sortedKeys behavior.
         */
        private fun sortJsonElement(element: com.google.gson.JsonElement): com.google.gson.JsonElement {
            return when {
                element.isJsonObject -> {
                    val obj = com.google.gson.JsonObject()
                    element.asJsonObject.entrySet()
                        .sortedBy { it.key }
                        .forEach { (key, value) ->
                            obj.add(key, sortJsonElement(value))
                        }
                    obj
                }
                element.isJsonArray -> {
                    val arr = com.google.gson.JsonArray()
                    element.asJsonArray.forEach { item ->
                        arr.add(sortJsonElement(item))
                    }
                    arr
                }
                else -> element
            }
        }
    }
}
