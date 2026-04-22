package com.vitalo.markrun.common.http.signature

import android.util.Base64
import com.vitalo.markrun.config.AppConfig
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import okio.Buffer
import java.nio.charset.Charset
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.DESKeySpec

class NewStoreCryptoInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()

        // 只有向 adBaseUrl 发起的 POST 请求才加密
        val isAdUrl = request.url.host.contains("newstorelite")
        if (isAdUrl && request.method == "POST") {
            val body = request.body
            if (body != null) {
                // 如果我们在这里将请求体消费了，为了避免流断开或者产生 Connection Reset 错误
                // 必须妥善地读取，这里用 buffer.writeTo 读取完整内容
                val buffer = Buffer()
                body.writeTo(buffer)
                val charset = body.contentType()?.charset(Charset.forName("UTF-8")) ?: Charset.forName("UTF-8")
                val jsonStr = buffer.readString(charset)

                val encryptedStr = encryptDES(jsonStr, AppConfig.adDesKey)
                if (encryptedStr != null) {
                    val bytes = encryptedStr.toByteArray(Charset.forName("UTF-8"))
                    val newBody = okhttp3.RequestBody.create("application/json".toMediaTypeOrNull(), bytes)
                    request = request.newBuilder()
                        .method(request.method, newBody)
                        .header("X-Crypto", "des")
                        // 移除原来的 Content-Length，因为修改了body大小
                        .removeHeader("Content-Length")
                        .build()
                }
            }
        }

        // 获取 body 时，如果是 POST 并且在拦截链中（此时还没到 Crypto），由于 `BaseSignatureConfigurator`
        // 内部通过 `getBodyParam` 读取 body 时可能会消费掉流，我们需要注意 OkHttp 拦截器的顺序。
        // 原来报错 Connection Reset 可能是因为我们在 NewStoreCryptoInterceptor 里读取 Body 时用的是 `writeTo` 或消耗了 buffer，
        // 或者在 SignatureInterceptor 里面读取 body 时消耗了 stream。
        
        var response = chain.proceed(request)

        // 拦截 adBaseUrl 的响应进行解密
        if (isAdUrl && response.isSuccessful) {
            val responseBody = response.body
            if (responseBody != null) {
                // 读取 bytes 并将 body 重建（避免只能读一次）
                val bytes = responseBody.bytes()
                if (bytes.isNotEmpty()) {
                    val decryptedJson = decryptDESBytes(bytes, AppConfig.adDesKey)

                    android.util.Log.d("NewStoreCrypto", "Decrypted JSON: $decryptedJson")

                    if (decryptedJson != null) {
                        val newResponseBody = decryptedJson.toResponseBody("application/json".toMediaTypeOrNull())
                        response = response.newBuilder()
                            .body(newResponseBody)
                            .build()
                    } else {
                        // 解密失败时原样返回
                        val newResponseBody = bytes.toResponseBody(responseBody.contentType())
                        response = response.newBuilder()
                            .body(newResponseBody)
                            .build()
                    }
                } else {
                    val newResponseBody = bytes.toResponseBody(responseBody.contentType())
                    response = response.newBuilder()
                        .body(newResponseBody)
                        .build()
                }
            }
        }

        return response
    }

    private fun encryptDES(data: String, key: String): String? {
        return try {
            val decodedKey = decodeDesKey(key) ?: return null
            val inputData = data.toByteArray(Charset.forName("UTF-8"))

            val desKeySpec = DESKeySpec(decodedKey)
            val keyFactory = SecretKeyFactory.getInstance("DES")
            val secretKey = keyFactory.generateSecret(desKeySpec)

            val cipher = Cipher.getInstance("DES/ECB/PKCS5Padding")
            cipher.init(Cipher.ENCRYPT_MODE, secretKey)

            val encryptedBytes = cipher.doFinal(inputData)
            val base64String = Base64.encodeToString(encryptedBytes, Base64.NO_WRAP)
            urlSafeBase64Encode(base64String)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun decryptDESBytes(decodedData: ByteArray, key: String): String? {
        return try {
            val decodedKey = decodeDesKey(key) ?: return null

            val desKeySpec = DESKeySpec(decodedKey)
            val keyFactory = SecretKeyFactory.getInstance("DES")
            val secretKey = keyFactory.generateSecret(desKeySpec)

            val cipher = Cipher.getInstance("DES/ECB/PKCS5Padding")
            cipher.init(Cipher.DECRYPT_MODE, secretKey)

            val decryptedBytes = cipher.doFinal(decodedData)
            String(decryptedBytes, Charset.forName("UTF-8"))
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun decryptDES(data: String, key: String): String? {
        return try {
            val decodedData = urlSafeBase64DecodeToByteArray(data) ?: return null
            val decodedKey = decodeDesKey(key) ?: return null

            val desKeySpec = DESKeySpec(decodedKey)
            val keyFactory = SecretKeyFactory.getInstance("DES")
            val secretKey = keyFactory.generateSecret(desKeySpec)

            val cipher = Cipher.getInstance("DES/ECB/PKCS5Padding")
            cipher.init(Cipher.DECRYPT_MODE, secretKey)

            val decryptedBytes = cipher.doFinal(decodedData)
            String(decryptedBytes, Charset.forName("UTF-8"))
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun decodeDesKey(key: String): ByteArray? {
        return try {
            val normalizedKey = normalizeBase64(key).replace("=", "_")
            val decodedKeyWithPadding: ByteArray = try {
                val base64Key = normalizedKey.replace("_", "=")
                Base64.decode(base64Key, Base64.NO_WRAP)
            } catch (e: Exception) {
                key.toByteArray(Charset.forName("UTF-8"))
            }

            val shouldIntercept = decodedKeyWithPadding.size > 8
            val shouldPad = decodedKeyWithPadding.size < 8

            if (shouldIntercept || shouldPad) {
                if (shouldIntercept) {
                    decodedKeyWithPadding.copyOf(8)
                } else {
                    val result = ByteArray(8)
                    System.arraycopy(decodedKeyWithPadding, 0, result, 0, decodedKeyWithPadding.size)
                    result
                }
            } else {
                decodedKeyWithPadding
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun normalizeBase64(input: String): String {
        var result = input
        while (result.length % 4 != 0) {
            result += "="
        }
        return result
    }

    private fun urlSafeBase64Encode(base64String: String): String {
        return try {
            val decodedData = Base64.decode(base64String, Base64.NO_WRAP)
            Base64.encodeToString(decodedData, Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING)
        } catch (e: Exception) {
            base64String.replace("+", "-").replace("/", "_").replace("=", "")
        }
    }

    private fun urlSafeBase64DecodeToByteArray(encodedString: String): ByteArray? {
        return try {
            var normalized = encodedString.replace("-", "+").replace("_", "/")
            while (normalized.length % 4 != 0) {
                normalized += "="
            }
            Base64.decode(normalized, Base64.NO_WRAP)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}