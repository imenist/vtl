package com.vitalo.markrun.common.http.host

import android.util.Log
import com.vitalo.markrun.config.AppConfig
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.Interceptor
import okhttp3.Response

class BaseUrlInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originRequest = chain.request()
        if (originRequest.headers[BaseUrlHeader.TAG] == BaseUrlHeader.NOT_INTERCEPTOR) {
            return chain.proceed(originRequest)
        }
        val builder = originRequest.newBuilder()
        val hostValues = originRequest.headers(BaseUrlHeader.KEY)
        if (hostValues.isEmpty() || hostValues[0].isBlank()) {
            throw IllegalArgumentException("lack of Host info, add HostHeader.")
        }

        Log.i(TAG, "intercept: url header value = $hostValues")
        builder.removeHeader(BaseUrlHeader.KEY)
        val replaceUrl = when (hostValues[0]) {
            BaseUrlHeader.URL_TYPE_ILLUS -> AppConfig.illusBaseUrl
            BaseUrlHeader.URL_TYPE_AD -> AppConfig.adBaseUrl
            BaseUrlHeader.URL_TYPE_AB -> AppConfig.abTestBaseUrl
            BaseUrlHeader.URL_TYPE_STAT -> AppConfig.statBaseUrl
            BaseUrlHeader.URL_TYPE_ELEPHANT -> AppConfig.elephantBaseUrl
            BaseUrlHeader.URL_TYPE_ACCOUNT -> AppConfig.accountBaseUrl
            else -> throw IllegalStateException("unknown url type: ${hostValues[0]}")
        }
        val host = replaceUrl.toHttpUrl()
        val httpUrl = originRequest.url.newBuilder()
            .scheme(host.scheme)
            .host(host.host)
            .port(host.port)
            .build()
        val newRequest = builder.url(httpUrl).build()
        Log.i(TAG, "intercept: new http url = ${newRequest.url}")
        return chain.proceed(newRequest)
    }

    companion object {
        private const val TAG = "BaseUrlInterceptor"
    }
}
