package com.vitalo.markrun.data.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.provider.Settings
import android.util.Base64
import com.google.gson.Gson
import com.vitalo.markrun.BuildConfig
import java.util.Locale

data class DeviceInfo(
    val version_number: Int,
    val country: String,
    val lang: String,
    val device_id: String,
    val type: String = "1",
    val phone_model: String,
    val net_type: String,
    val user_group: Int = 1,
    val user_channel: Int = 1
) {
    fun toBase64(): String {
        val json = Gson().toJson(this)
        return Base64.encodeToString(json.toByteArray(), Base64.NO_WRAP)
    }

    companion object {
        fun create(context: Context): DeviceInfo {
            return DeviceInfo(
                version_number = BuildConfig.VERSION_CODE,
                country = Locale.getDefault().country,
                lang = Locale.getDefault().language,
                device_id = Settings.Secure.getString(
                    context.contentResolver,
                    Settings.Secure.ANDROID_ID
                ) ?: "unknown",
                phone_model = Build.MODEL,
                net_type = getNetworkType(context)
            )
        }

        private fun getNetworkType(context: Context): String {
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val network = cm.activeNetwork ?: return "UNKNOWN"
            val capabilities = cm.getNetworkCapabilities(network) ?: return "UNKNOWN"
            return when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> "WIFI"
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> "4G"
                else -> "UNKNOWN"
            }
        }
    }
}
