package com.vitalo.markrun.util

import android.content.Context
import android.os.Parcelable
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.tencent.mmkv.MMKV

object MmkvUtils {

    private val instance: MMKV by lazy { MMKV.defaultMMKV() }
    val gson: Gson by lazy { Gson() }

    fun initialize(context: Context) {
        MMKV.initialize(context)
    }

    fun putString(key: String, value: String?) = instance.encode(key, value)

    fun putInt(key: String, value: Int) = instance.encode(key, value)

    fun putInteger(key: String, value: Int) = instance.encode(key, value)

    fun putBoolean(key: String, value: Boolean) = instance.encode(key, value)

    fun putFloat(key: String, value: Float) = instance.encode(key, value)

    fun putLong(key: String, value: Long) = instance.encode(key, value)

    fun getBool(key: String) = instance.decodeBool(key)

    fun putDouble(key: String, value: Double) = instance.encode(key, value)

    fun putStringSet(key: String, value: Set<String>) = instance.encode(key, value)

    fun putParcelable(key: String, value: Parcelable?) = instance.encode(key, value)

    fun putBytes(key: String, value: ByteArray?) = instance.encode(key, value)

    fun getString(key: String): String? = instance.decodeString(key)

    fun getString(key: String, default: String): String = instance.decodeString(key, default) ?: default

    fun getInt(key: String): Int = instance.decodeInt(key)

    fun getInt(key: String, default: Int): Int = instance.decodeInt(key, default)

    fun getFloat(key: String): Float = instance.decodeFloat(key)

    fun getFloat(key: String, default: Float): Float = instance.decodeFloat(key, default)

    fun getBoolean(key: String): Boolean = instance.decodeBool(key)

    fun getBoolean(key: String, default: Boolean): Boolean = instance.decodeBool(key, default)

    fun getStringSet(key: String): Set<String> = instance.decodeStringSet(key, HashSet()) ?: HashSet()

    fun getStringSet(key: String, defaultValue: MutableSet<String>): MutableSet<String> =
        instance.decodeStringSet(key, defaultValue) ?: defaultValue

    fun getDouble(key: String): Double = instance.decodeDouble(key)

    fun getDouble(key: String, default: Double): Double = instance.decodeDouble(key, default)

    fun getLong(key: String): Long = instance.decodeLong(key)

    fun getLong(key: String, default: Long): Long = instance.decodeLong(key, default)

    fun getBytes(key: String): ByteArray? = instance.decodeBytes(key)

    fun <T : Parcelable> getParcelable(key: String, clz: Class<T>): T? = instance.decodeParcelable(key, clz)

    fun remove(key: String) = instance.remove(key)

    fun containsKey(key: String): Boolean = instance.containsKey(key)

    fun clearAll() = instance.clearAll()

    fun allKeys(): Array<String>? = instance.allKeys()

    inline fun <reified T> putObject(key: String, value: T?) {
        val json = if (value == null) null else gson.toJson(value)
        putString(key, json)
    }

    inline fun <reified T> getObject(key: String): T? {
        val json = getString(key) ?: return null
        return try {
            val type = object : TypeToken<T>() {}.type
            gson.fromJson<T>(json, type)
        } catch (e: Exception) {
            null
        }
    }

    inline fun <reified T> getList(key: String): List<T> {
        val json = getString(key) ?: return emptyList()
        return try {
            val type = object : TypeToken<List<T>>() {}.type
            gson.fromJson<List<T>>(json, type) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    inline fun <reified T> putList(key: String, list: List<T>?) {
        val json = if (list == null) null else gson.toJson(list)
        putString(key, json)
    }
}
