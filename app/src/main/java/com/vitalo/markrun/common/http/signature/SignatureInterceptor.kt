package com.vitalo.markrun.common.http.signature

import android.content.Context
import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response

class SignatureInterceptor(private val context: Context) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val configurator = SignatureConfiguratorFactory.getConfigurator(
            context,
            request.url.scheme + "://" + request.url.host
        )

        val newRequest = configurator.getNewRequest(request)
        Log.i(TAG, "intercept: $newRequest")
        return configurator.getNewResponse(chain.proceed(newRequest))
    }

    companion object {
        private const val TAG = "SignatureInterceptor"
    }
}
