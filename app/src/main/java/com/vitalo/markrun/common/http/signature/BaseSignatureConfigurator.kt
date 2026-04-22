package com.vitalo.markrun.common.http.signature

import android.content.Context
import com.vitalo.markrun.config.AppConfig
import com.vitalo.markrun.data.network.DeviceInfo
import com.vitalo.markrun.data.network.Signature
import okhttp3.HttpUrl
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import okio.Buffer
import java.nio.charset.Charset

abstract class BaseSignatureConfigurator(protected val context: Context) : ISignatureConfigurator {
    protected val SIGNATURE_KEY = "X-Signature"
    protected val CONTENT_TYPE = "Content-Type"
    protected abstract val apiKey: String
    protected abstract val apiSecret: String

    open fun generateDeviceParam(): String {
        return DeviceInfo.create(context).toBase64()
    }

    open fun configRequestBuilder(builder: Request.Builder) {
    }

    override fun getNewRequest(request: Request): Request {
        val timestamp = System.currentTimeMillis()
        val device = generateDeviceParam()
        val newUrl = request.url.newBuilder()
            .addQueryParameter("api_key", apiKey)
            .addQueryParameter("device", device)
            .addQueryParameter("timestamp", timestamp.toString())
            .build()

        val builder = request.newBuilder()
            .url(newUrl)
            .addHeader(CONTENT_TYPE, "application/json")
            .addHeader("X-Source", AppConfig.packageName)

        builder.addHeader(SIGNATURE_KEY, getApiSignature(request, newUrl))
        
        configRequestBuilder(builder)
        return builder.build()
    }

    protected fun getApiSignature(request: Request, url: HttpUrl): String {
        val uri = url.toUri().path
        val secret = apiSecret
        val queryStr = url.encodedQuery ?: ""
        return when (request.method) {
            "POST" -> Signature.postSign(uri, secret, queryStr, getBodyParam(request.body))
            "GET" -> Signature.getSign(uri, secret, queryStr, "")
            else -> throw IllegalStateException("request method -> ${request.method} not support yet")
        }
    }

    protected fun getBodyParam(body: RequestBody?): String {
        return if (body != null) {
            try {
                // 如果是加密拦截器后面执行，那可能获取的是密文，但在目前的排列里，
                // HttpClient 中的拦截器顺序是： SignatureInterceptor -> NewStoreCryptoInterceptor
                // 所以这里获取到的是明文的 JSON，这是正确的，和 MoveFit 一致。
                val buffer = Buffer()
                body.writeTo(buffer)
                var charset = Charset.forName("UTF-8")
                val contentType = body.contentType()
                if (contentType != null) {
                    charset = contentType.charset(charset) ?: Charset.forName("UTF-8")
                }
                val result = buffer.readString(charset)
                android.util.Log.d("SignatureInterceptor", "算签名的请求体内容: $result")
                result
            } catch (e: Exception) {
                ""
            }
        } else {
            ""
        }
    }

    override fun getNewResponse(response: Response): Response {
        return response.newBuilder().header("Server-Encrypt", "true").build()
    }
}
