package com.vitalo.markrun.common.cache

import android.content.Context
import java.io.File

object VideoCacheManager {
    private const val EXTENSION = ".mp4"

    fun getLocalFile(context: Context, remoteUrl: String): File {
        return FileCacheManager.getLocalFile(context, remoteUrl, EXTENSION)
    }

    fun isVideoCached(context: Context, remoteUrl: String): Boolean {
        return FileCacheManager.isFileCached(context, remoteUrl, EXTENSION)
    }

    suspend fun downloadVideo(
        context: Context,
        remoteUrl: String,
        onProgress: (Double) -> Unit
    ): File? {
        return FileCacheManager.downloadFileWithProgress(context, remoteUrl, EXTENSION, onProgress)
    }
}
