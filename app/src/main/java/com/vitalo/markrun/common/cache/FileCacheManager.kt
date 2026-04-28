package com.vitalo.markrun.common.cache

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream
import java.security.MessageDigest
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit

object FileCacheManager {
    private val activeTasks = ConcurrentHashMap<String, Boolean>()

    private val okHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .followRedirects(true)
            .followSslRedirects(true)
            .build()
    }

    private fun hashString(input: String): String {
        return try {
            val digest = MessageDigest.getInstance("SHA-256")
            val hashBytes = digest.digest(input.toByteArray())
            hashBytes.joinToString("") { "%02x".format(it) }
        } catch (e: Exception) {
            input.hashCode().toString()
        }
    }

    fun getLocalFileName(remoteUrl: String, extension: String): String {
        return hashString(remoteUrl) + extension
    }

    fun getLocalFile(context: Context, remoteUrl: String, extension: String): File {
        val fileName = getLocalFileName(remoteUrl, extension)
        val cacheDir = context.cacheDir
        return File(cacheDir, fileName)
    }

    fun isFileCached(context: Context, remoteUrl: String, extension: String): Boolean {
        val file = getLocalFile(context, remoteUrl, extension)
        return file.exists() && file.length() > 0
    }

    fun cancelDownload(remoteUrl: String) {
        activeTasks.remove(remoteUrl)
    }

    fun cancelAllDownloads() {
        activeTasks.clear()
    }

    suspend fun downloadFileWithProgress(
        context: Context,
        remoteUrl: String,
        extension: String,
        onProgress: (Double) -> Unit
    ): File? = withContext(Dispatchers.IO) {
        val file = getLocalFile(context, remoteUrl, extension)
        if (file.exists() && file.length() > 0) {
            withContext(Dispatchers.Main) { onProgress(1.0) }
            return@withContext file
        }

        // Cancel existing task if any
        cancelDownload(remoteUrl)
        activeTasks[remoteUrl] = true

        try {
            val request = Request.Builder().url(remoteUrl).build()
            val response = okHttpClient.newCall(request).execute()

            if (!response.isSuccessful) {
                activeTasks.remove(remoteUrl)
                return@withContext null
            }

            val body = response.body
            if (body == null) {
                activeTasks.remove(remoteUrl)
                return@withContext null
            }

            val fileLength = body.contentLength()
            val inputStream = body.byteStream()
            
            // Download to a temporary file first
            val tempFile = File(context.cacheDir, file.name + ".tmp")
            val outputStream = FileOutputStream(tempFile)

            val data = ByteArray(8192)
            var total: Long = 0
            var count: Int
            
            var lastReportedProgress = -1.0

            while (inputStream.read(data).also { count = it } != -1) {
                // Check if task was cancelled
                if (activeTasks[remoteUrl] != true) {
                    outputStream.close()
                    inputStream.close()
                    tempFile.delete()
                    return@withContext null
                }
                
                total += count.toLong()
                if (fileLength > 0) {
                    val progress = total.toDouble() / fileLength.toDouble()
                    // Only update UI if progress changed by more than 1% to prevent context switch spam
                    if (progress - lastReportedProgress > 0.01 || progress >= 1.0) {
                        lastReportedProgress = progress
                        withContext(Dispatchers.Main) {
                            onProgress(progress)
                        }
                    }
                }
                outputStream.write(data, 0, count)
            }

            outputStream.flush()
            outputStream.close()
            inputStream.close()
            
            // Rename temp file to actual file
            if (file.exists()) {
                file.delete()
            }
            tempFile.renameTo(file)
            
            activeTasks.remove(remoteUrl)
            return@withContext file
        } catch (e: Exception) {
            e.printStackTrace()
            activeTasks.remove(remoteUrl)
            return@withContext null
        }
    }
}
