package com.vitalo.markrun.common.http.signature

import android.content.Context
import com.vitalo.markrun.config.AppConfig

class IllusConfigurator(context: Context) : BaseSignatureConfigurator(context) {
    override val apiKey: String
        get() = AppConfig.illusApiKey
    override val apiSecret: String
        get() = AppConfig.illusApiSecret
}
