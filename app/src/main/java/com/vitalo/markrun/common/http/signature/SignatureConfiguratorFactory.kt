package com.vitalo.markrun.common.http.signature

import android.content.Context
import com.vitalo.markrun.config.AppConfig

object SignatureConfiguratorFactory {

    fun getConfigurator(context: Context, baseUrl: String): ISignatureConfigurator =
        when (baseUrl) {
            AppConfig.illusBaseUrl -> IllusConfigurator(context)
            AppConfig.adBaseUrl -> NewStoreConfigurator(context)
            else -> EmptySignatureConfigurator()
        }
}
