package com.vitalo.markrun.common.http.signature

import android.util.Log
import com.vitalo.markrun.AppStateManager
import okhttp3.Interceptor
import okhttp3.Response
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

/**
 * 服务器时间响应拦截器
 * 用于获取服务端响应中的时间并同步本地记录的服务器时间
 */
class ServiceTimeInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())
        response.header("Date")?.let { dateString ->
            try {
                // 解析 HTTP Date 格式 (RFC 1123): "Mon, 29 Dec 2025 02:42:23 GMT"
                val dateFormat = SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US)
                dateFormat.timeZone = TimeZone.getTimeZone("GMT")
                val date = dateFormat.parse(dateString)
                date?.time?.let { serverTimeMillis ->
                    AppStateManager.updateServiceTime(serverTimeMillis)
                    Log.d("ServiceTimeInterceptor", "服务器时间已更新: $serverTimeMillis ($dateString)")
                }
            } catch (e: Exception) {
                Log.e("ServiceTimeInterceptor", "解析服务器时间失败: ${e.message}")
            }
        }
        return response
    }
}