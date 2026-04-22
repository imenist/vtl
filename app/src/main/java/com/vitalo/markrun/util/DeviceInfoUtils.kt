package com.vitalo.markrun.util

import android.content.Context
import android.os.Build
import android.provider.Settings
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.Locale
import java.util.TimeZone
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DeviceInfoUtils @Inject constructor(
    @ApplicationContext private val context: Context
) {
    fun getDeviceId(): String {
        return Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
            ?: UUID.randomUUID().toString()
    }

    /**
     * Matches iOS DeviceInfoUtils.getDeviceInfoMap() field names exactly:
     * - app_version: Int (build code)
     * - device_type: 1 for Android (iOS sends 2)
     * - phone_model: device model string
     * - device_id: Android ID
     * - system_version: OS version string
     * - package_name: application package name
     * - channel_type: 1 for Android (iOS sends 2)
     * - language: 2-char lowercase language code
     * - sim_country: country code
     * - zone: timezone in GMT format
     */
    fun getDeviceInfoMap(): Map<String, Any> {
        return mapOf(
            "app_version" to getAppVersionCode(),
            "device_type" to 1,
            "phone_model" to "${Build.MANUFACTURER} ${Build.MODEL}",
            "device_id" to getDeviceId(),
            "system_version" to Build.VERSION.RELEASE,
            "package_name" to context.packageName,
            "channel_type" to 1,
            "language" to Locale.getDefault().language.lowercase().take(2),
            "sim_country" to Locale.getDefault().country.uppercase(),
            "zone" to getTimezone(),
            "net_type" to "WiFi",
            "using_vpn" to 2,
            "has_sim" to 2
        )
    }

    private fun getAppVersionCode(): Int {
        return try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                packageInfo.longVersionCode.toInt()
            } else {
                @Suppress("DEPRECATION")
                packageInfo.versionCode
            }
        } catch (_: Exception) {
            1
        }
    }

    fun getAppVersionName(): String {
        return try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            packageInfo.versionName ?: "1.0"
        } catch (_: Exception) {
            "1.0"
        }
    }

    private fun getTimezone(): String {
        val offsetMs = TimeZone.getDefault().rawOffset
        val hours = offsetMs / (1000 * 60 * 60)
        return String.format("GMT%+d", hours)
    }
}
