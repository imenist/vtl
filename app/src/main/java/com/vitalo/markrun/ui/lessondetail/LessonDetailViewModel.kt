package com.vitalo.markrun.ui.lessondetail

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vitalo.markrun.common.cache.AudioCacheManager
import com.vitalo.markrun.common.cache.FileCacheManager
import com.vitalo.markrun.common.cache.VideoCacheManager
import com.vitalo.markrun.data.remote.model.Lesson
import com.vitalo.markrun.data.repository.TrainingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

enum class DownloadState {
    IDLE,
    DOWNLOADING,
    COMPLETED,
    FAILED
}

data class MediaItem(
    val url: String,
    val type: String // "video" or "audio"
)

@HiltViewModel
class LessonDetailViewModel @Inject constructor(
    private val repository: TrainingRepository
) : ViewModel() {

    private val _lesson = MutableStateFlow<Lesson?>(null)
    val lesson: StateFlow<Lesson?> = _lesson.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isError = MutableStateFlow(false)
    val isError: StateFlow<Boolean> = _isError.asStateFlow()

    private val _downloadState = MutableStateFlow(DownloadState.IDLE)
    val downloadState: StateFlow<DownloadState> = _downloadState.asStateFlow()

    private val _downloadProgress = MutableStateFlow(0.0)
    val downloadProgress: StateFlow<Double> = _downloadProgress.asStateFlow()

    private val _downloadError = MutableStateFlow<String?>(null)
    val downloadError: StateFlow<String?> = _downloadError.asStateFlow()

    var isViewActive: Boolean = true
    var isNavigatingToChild: Boolean = false

    private var currentCode: String? = null
    private var downloadToken: UUID? = null

    fun cancelDownloads() {
        FileCacheManager.cancelAllDownloads()
        downloadToken = null
        _downloadState.value = DownloadState.IDLE
        _downloadProgress.value = 0.0
        _downloadError.value = null
    }

    fun load(code: String) {
        currentCode = code
        _isLoading.value = true
        _isError.value = false

        viewModelScope.launch {
            try {
                val response = repository.fetchLessonDetail(code)
                if (response.isSuccess && response.data != null) {
                    _lesson.value = response.data
                } else {
                    _lesson.value = null
                    _isError.value = true
                }
            } catch (_: Exception) {
                _isError.value = true
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun reload() {
        currentCode?.let { load(it) }
    }

    fun checkAndDownloadAllMedia(context: Context, onComplete: (Boolean) -> Unit) {
        val currentToken = UUID.randomUUID()
        downloadToken = currentToken

        val actions = _lesson.value?.mountActions ?: emptyList()
        val videoUrls = actions.mapNotNull { it.action }
            .flatMap { it.videos ?: emptyList() }
            .mapNotNull { it.videoUrl }
            .filter { it.isNotEmpty() }

        val mp3Urls = actions.mapNotNull { it.action?.voiceCoachMp3 }
            .filter { it.isNotEmpty() }

        val allUrls = (videoUrls.map { MediaItem(it, "video") } + 
                       mp3Urls.map { MediaItem(it, "audio") }).distinctBy { it.url }

        if (allUrls.isEmpty()) {
            _downloadState.value = DownloadState.COMPLETED
            _downloadProgress.value = 1.0
            onComplete(true)
            return
        }

        val urlsToDownload = allUrls.filter { item ->
            if (item.type == "video") {
                !VideoCacheManager.isVideoCached(context, item.url)
            } else {
                !AudioCacheManager.isAudioCached(context, item.url)
            }
        }

        if (urlsToDownload.isEmpty()) {
            _downloadState.value = DownloadState.COMPLETED
            _downloadProgress.value = 1.0
            onComplete(true)
            return
        }

        _downloadState.value = DownloadState.DOWNLOADING
        _downloadProgress.value = 0.0
        _downloadError.value = null

        viewModelScope.launch(Dispatchers.IO) {
            val total = urlsToDownload.size
            val progresses = DoubleArray(total) { 0.0 }
            var hasFailed = false

            val deferredTasks = urlsToDownload.mapIndexed { index, item ->
                async {
                    if (downloadToken != currentToken) return@async null

                    val result = if (item.type == "video") {
                        VideoCacheManager.downloadVideo(context, item.url) { progress ->
                            if (downloadToken == currentToken) {
                                progresses[index] = progress
                                val avg = progresses.sum() / total.toDouble()
                                _downloadProgress.value = avg
                            }
                        }
                    } else {
                        AudioCacheManager.downloadAudio(context, item.url) { progress ->
                            if (downloadToken == currentToken) {
                                progresses[index] = progress
                                val avg = progresses.sum() / total.toDouble()
                                _downloadProgress.value = avg
                            }
                        }
                    }

                    if (result == null) {
                        hasFailed = true
                    }
                    result
                }
            }

            deferredTasks.awaitAll()

            launch(Dispatchers.Main) {
                if (downloadToken != currentToken) return@launch
                
                if (!isViewActive) {
                    return@launch
                }

                if (hasFailed) {
                    _downloadState.value = DownloadState.FAILED
                    _downloadError.value = "有文件下载失败，请重试"
                    onComplete(false)
                } else {
                    _downloadState.value = DownloadState.COMPLETED
                    _downloadProgress.value = 1.0
                    onComplete(true)
                }
            }
        }
    }
}
