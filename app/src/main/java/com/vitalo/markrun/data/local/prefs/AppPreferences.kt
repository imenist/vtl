package com.vitalo.markrun.data.local.prefs

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppPreferences @Inject constructor(
    @ApplicationContext context: Context
) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("vitalo_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    companion object {
        const val KEY_HAS_LAUNCHED_BEFORE = "hasLaunchedBefore"
        const val KEY_CURRENT_USER = "currentUser"
        const val KEY_AUTO_LOGIN_RESULT = "autoLoginResult"
        const val KEY_AUTO_LOGIN_SAVED_TIME = "autoLoginResultSavedTime"
        const val KEY_DISTINCT_ID = "distinctId"
        const val KEY_DEVICE_ID = "deviceId"
        const val KEY_INSTALL_DATE = "installDate"
        const val KEY_STEP_GOAL = "stepGoal"
        const val KEY_AB_TEST_RESULT = "abTestResult"
        const val KEY_SELECTED_DISTANCE_UNIT = "selectedDistanceUnit"
        const val KEY_FRAGMENT_PROGRESS = "fragmentProgress"
        const val KEY_HAS_SHOWN_LOCATION_PERMISSION = "hasShownLocationPermission"
    }

    fun setBoolean(key: String, value: Boolean) = prefs.edit().putBoolean(key, value).apply()
    fun getBoolean(key: String, default: Boolean = false): Boolean = prefs.getBoolean(key, default)

    fun setString(key: String, value: String?) = prefs.edit().putString(key, value).apply()
    fun getString(key: String): String? = prefs.getString(key, null)

    fun setInt(key: String, value: Int) = prefs.edit().putInt(key, value).apply()
    fun getInt(key: String, default: Int = 0): Int = prefs.getInt(key, default)

    fun setLong(key: String, value: Long) = prefs.edit().putLong(key, value).apply()
    fun getLong(key: String, default: Long = 0L): Long = prefs.getLong(key, default)

    fun setFloat(key: String, value: Float) = prefs.edit().putFloat(key, value).apply()
    fun getFloat(key: String, default: Float = 0f): Float = prefs.getFloat(key, default)

    fun remove(key: String) = prefs.edit().remove(key).apply()

    fun <T> setCodable(key: String, value: T) {
        prefs.edit().putString(key, gson.toJson(value)).apply()
    }

    fun <T> getCodable(key: String, clazz: Class<T>): T? {
        val json = prefs.getString(key, null) ?: return null
        return try {
            gson.fromJson(json, clazz)
        } catch (_: Exception) {
            null
        }
    }

    val hasLaunchedBefore: Boolean
        get() = getBoolean(KEY_HAS_LAUNCHED_BEFORE)

    fun setHasLaunchedBefore(value: Boolean) = setBoolean(KEY_HAS_LAUNCHED_BEFORE, value)

    val hasShownLocationPermission: Boolean
        get() = getBoolean(KEY_HAS_SHOWN_LOCATION_PERMISSION)

    fun setHasShownLocationPermission(value: Boolean) = setBoolean(KEY_HAS_SHOWN_LOCATION_PERMISSION, value)

    fun getDistinctId(): String {
        var id = getString(KEY_DISTINCT_ID)
        if (id == null) {
            id = java.util.UUID.randomUUID().toString()
            setString(KEY_DISTINCT_ID, id)
        }
        return id
    }
}
