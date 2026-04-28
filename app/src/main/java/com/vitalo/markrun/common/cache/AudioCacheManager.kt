package com.vitalo.markrun.common.cache

import android.content.Context
import java.io.File

object AudioCacheManager {
    private const val EXTENSION = ".mp3"

    fun getLocalFile(context: Context, remoteUrl: String): File {
        return FileCacheManager.getLocalFile(context, remoteUrl, EXTENSION)
    }

    fun isAudioCached(context: Context, remoteUrl: String): Boolean {
        return FileCacheManager.isFileCached(context, remoteUrl, EXTENSION)
    }

    suspend fun downloadAudio(
        context: Context,
        remoteUrl: String,
        onProgress: (Double) -> Unit
    ): File? {
        return FileCacheManager.downloadFileWithProgress(context, remoteUrl, EXTENSION, onProgress)
    }
}
