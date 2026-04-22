package com.vitalo.markrun.common.http

import android.app.Application
import com.vitalo.markrun.config.AppConfig
import com.vitalo.markrun.util.DebugLogContainer
import com.vitalo.markrun.util.LogUtils
import com.vitalo.markrun.common.http.host.BaseUrlInterceptor
import com.vitalo.markrun.common.http.signature.NewStoreCryptoInterceptor
import com.vitalo.markrun.common.http.signature.ServiceTimeInterceptor
import com.vitalo.markrun.common.http.signature.SignatureInterceptor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.InputStream
import java.util.concurrent.TimeUnit

object HttpClient {

    private lateinit var context: Application

    fun init(context: Application) {
        this.context = context
    }

    private fun client(): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .addInterceptor(BaseUrlInterceptor())
            .addInterceptor(HttpLoggingInterceptor(object : HttpLoggingInterceptor.Logger {
                override fun log(message: String) {
                    if (LogUtils.OPEN) {
                        val maxLogSize = 1000
                        for (i in 0..message.length / maxLogSize) {
                            val start = i * maxLogSize
                            val end = minOf((i + 1) * maxLogSize, message.length)
                            LogUtils.log(TAG, message.substring(start, end))
                        }
                    }
                    DebugLogContainer.putHttpLog(message)
                }
            }).setLevel(HttpLoggingInterceptor.Level.BODY))
            // 先进行签名（基于明文）
            .addInterceptor(SignatureInterceptor(context))
            // 后进行加密（将明文转密文并发送）
            .addInterceptor(NewStoreCryptoInterceptor())
            // 添加服务器时间拦截器
            .addInterceptor(ServiceTimeInterceptor())
            .build()
    }

    private val retrofit: Retrofit by lazy {
        val gson = com.google.gson.GsonBuilder()
            .disableHtmlEscaping()
            .serializeNulls()
            .setLenient() // 允许宽松的 JSON 解析，避免 malformed JSON 报错
            .create()
        Retrofit.Builder()
            // 使用自定义的 Gson 以保证 Null 和特殊字符不丢失
            .addConverterFactory(GsonConverterFactory.create(gson))
            .baseUrl(AppConfig.illusBaseUrl)
            .client(client())
            .build()
    }

    fun <T> createService(serviceClass: Class<T>): T {
        return retrofit.create(serviceClass)
    }

    suspend fun downloadFile(url: String): InputStream? {
        return withContext(Dispatchers.IO) {
            try {
                val okHttpClient = OkHttpClient()
                val request = Request.Builder().url(url).build()
                val response = okHttpClient.newCall(request).execute()
                if (response.isSuccessful && response.body != null) {
                    response.body!!.byteStream()
                } else {
                    null
                }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    suspend fun downloadFile(
        outputFile: File,
        url: String,
        process: ((Int) -> Unit)? = null,
        callback: ((Boolean) -> Unit)? = null
    ) {
        withContext(Dispatchers.IO) {
            try {
                val okHttpClient = OkHttpClient()
                val request = Request.Builder().url(url).build()
                val response = okHttpClient.newCall(request).execute()
                val contentLength = response.headers["Content-Length"]?.toLongOrNull() ?: 300000L
                val byteStream = response.body?.byteStream() ?: return@withContext
                byteStream.use { inputStream ->
                    outputFile.outputStream().use { stream ->
                        val buffer = ByteArray(1024)
                        var bytesRead: Int
                        var bytesDownloaded: Long = 0

                        while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                            stream.write(buffer, 0, bytesRead)
                            bytesDownloaded += bytesRead
                            process?.invoke((bytesDownloaded / contentLength.toFloat() * 100).toInt())
                        }
                    }
                }
                callback?.invoke(true)
            } catch (e: Exception) {
                e.printStackTrace()
                callback?.invoke(false)
            }
        }
    }

    private const val TAG = "HttpClient"
}
