package com.vitalo.markrun.data.remote.model

import android.content.Context
import android.os.Build
import android.provider.Settings
import com.google.gson.annotations.SerializedName
import com.vitalo.markrun.config.AppConfig
import java.util.Locale
import java.util.TimeZone

data class NewStoreDeviceInfo(
    @SerializedName("app_version") val appVersion: Int? = null,
    @SerializedName("device_type") val deviceType: Int? = null,
    @SerializedName("phone_model") val phoneModel: String? = null,
    @SerializedName("net_type") val netType: String? = null,
    @SerializedName("device_id") val deviceID: String? = null,
    @SerializedName("system_version") val systemVersion: String? = null,
    @SerializedName("package_name") val packageName: String? = null,
    @SerializedName("sim_country") val simCountry: String? = null,
    @SerializedName("language") val language: String? = null,
    @SerializedName("zone") val zone: String? = null,
    @SerializedName("channel") val channel: Int? = null,
    @SerializedName("cid") val cid: Int? = null,
    @SerializedName("activate_length") val activateLength: Int? = null
) {
    companion object {
        fun create(context: Context): NewStoreDeviceInfo {
            return NewStoreDeviceInfo(
                appVersion = AppConfig.versionCode,
                deviceType = 1,
                phoneModel = Build.MODEL,
                netType = "WIFI",
                deviceID = Settings.Secure.getString(
                    context.contentResolver, Settings.Secure.ANDROID_ID
                ) ?: "unknown",
                systemVersion = Build.VERSION.RELEASE,
                packageName = AppConfig.packageName,
                simCountry = Locale.getDefault().country,
                language = Locale.getDefault().language,
                zone = TimeZone.getDefault().id,
                cid = AppConfig.adCid,
                activateLength = 0
            )
        }
    }
}
