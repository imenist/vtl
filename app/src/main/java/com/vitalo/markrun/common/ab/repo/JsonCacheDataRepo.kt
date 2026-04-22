package com.vitalo.markrun.common.ab.repo

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import java.io.File
import java.lang.reflect.Type
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean

abstract class JsonCacheDataRepo<T>(val context: Context) : SimpleDataRepo<T>() {
    private val hasBlockingReq = AtomicBoolean(false)

    protected abstract fun getCacheSaveDir(): String
    protected abstract fun getDataTypeToken(): Type
    protected abstract fun getRepoIdentityKey(): String

    private fun getLatestCacheDataTimeKey(): String {
        return "json_repo_cache_time_${getRepoIdentityKey()}"
    }

    open fun buildJsonCacheKey(): String {
        return "${getRepoIdentityKey()}_cache.json"
    }

    private fun getCacheTime(): Long {
        return context.getSharedPreferences("ab_cache_prefs", Context.MODE_PRIVATE)
            .getLong(getLatestCacheDataTimeKey(), 0L)
    }

    private fun saveCacheTime(time: Long) {
        context.getSharedPreferences("ab_cache_prefs", Context.MODE_PRIVATE)
            .edit().putLong(getLatestCacheDataTimeKey(), time).apply()
    }

    override fun shouldFetchRemoteData(): Boolean {
        val loadStatus = getLoadStatus()
        if (loadStatus == FetchStatus.FAILED) {
            return true
        }
        if (loadStatus == FetchStatus.SUCCESS) {
            return false
        }
        if (loadStatus == FetchStatus.FETCHING) {
            return System.currentTimeMillis() - lastFetchTime > 10 * 1000
        }
        val lastCacheTime = getCacheTime()
        val diffMillis = System.currentTimeMillis() - lastCacheTime
        val cacheOverdueTime = TimeUnit.HOURS.toMillis(8)
        if (diffMillis > cacheOverdueTime) {
            Log.d(logTag, "缓存失效, 可以请求")
            return true
        } else {
            Log.d(logTag, "缓存有效, 屏蔽请求")
            hasBlockingReq.set(true)
            return false
        }
    }

    override fun fetchCacheData(callback: (cacheData: T?) -> Unit) {
        var jsonInvalidate = false
        val jsonFile = getJsonCacheFile()
        val jsonString = try {
            if (jsonFile.exists()) jsonFile.readText() else null
        } catch (e: Exception) { null }

        if (jsonString.isNullOrEmpty()) {
            jsonInvalidate = true
            callback(null)
        } else {
            val gson = createDeserializerGson()
            try {
                val data = gson.fromJson<T>(jsonString, getDataTypeToken())
                if (data == null) {
                    jsonInvalidate = true
                } else if (data is List<*> && data.isEmpty()) {
                    jsonInvalidate = true
                }
                onCacheLoaded(data)
                callback(data)
            } catch (ex: Exception) {
                callback(null)
                jsonInvalidate = true
            }
        }
        if (jsonInvalidate && hasBlockingReq.compareAndSet(true, false)) {
            Log.d(logTag, "Json缓存判断失效, 重新执行被拒绝的请求")
            enqueueTask {
                refreshRemoteData(true)
            }
        }
    }

    protected fun saveRemoteDataToCache(cacheData: T?, jsonStr: String? = null) {
        saveCacheTime(System.currentTimeMillis())
        enqueueTask {
            val jsonFile = getJsonCacheFile()
            try {
                if (cacheData == null && jsonStr == null) {
                    jsonFile.writeText("{}")
                } else {
                    val json = jsonStr ?: createDeserializerGson().toJson(cacheData)
                    jsonFile.writeText(json)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    open fun createDeserializerGson(): Gson {
        return Gson()
    }

    fun hasCacheData(): Boolean {
        if (getObservableData().value != null) {
            return true
        }
        return getCacheTime() != 0L
    }

    private fun getJsonCacheFile(): File {
        val dir = File(getCacheSaveDir())
        if (!dir.exists()) dir.mkdirs()
        return File(dir, buildJsonCacheKey())
    }

    open fun clearCacheConfig() {
        val cacheFile = getJsonCacheFile()
        if (cacheFile.exists()) cacheFile.delete()
        saveCacheTime(0L)
    }

    open fun onCacheLoaded(cache: T?) {
    }
}