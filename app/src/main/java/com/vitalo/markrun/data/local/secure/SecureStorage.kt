package com.vitalo.markrun.data.local.secure

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SecureStorage @Inject constructor(
    @ApplicationContext context: Context
) {
    private val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
    private val securePrefs: SharedPreferences = EncryptedSharedPreferences.create(
        "vitalo_secure_prefs",
        masterKeyAlias,
        context,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun setString(key: String, value: String?) {
        securePrefs.edit().putString(key, value).apply()
    }

    fun getString(key: String): String? = securePrefs.getString(key, null)

    fun setBoolean(key: String, value: Boolean) {
        securePrefs.edit().putBoolean(key, value).apply()
    }

    fun getBoolean(key: String, default: Boolean = false): Boolean =
        securePrefs.getBoolean(key, default)

    fun remove(key: String) {
        securePrefs.edit().remove(key).apply()
    }
}
