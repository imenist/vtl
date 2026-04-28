package com.vitalo.markrun.ui.followalong

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.vitalo.markrun.data.remote.model.MountAction
import com.vitalo.markrun.data.remote.model.Training
import javax.inject.Inject

@HiltViewModel
class FollowAlongViewModel @Inject constructor() : ViewModel() {

    private val _currentIndex = MutableStateFlow(0)
    val currentIndex: StateFlow<Int> = _currentIndex.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _isMuted = MutableStateFlow(false)
    val isMuted: StateFlow<Boolean> = _isMuted.asStateFlow()

    private val _progress = MutableStateFlow(0.0)
    val progress: StateFlow<Double> = _progress.asStateFlow()

    private val _bonusProgress = MutableStateFlow(0.0)
    val bonusProgress: StateFlow<Double> = _bonusProgress.asStateFlow()

    private var bonusElapsedSeconds = 0.0
    private val bonusDurationSeconds = 7.0

    private val _currentPlayedSeconds = MutableStateFlow(0.0)
    val currentPlayedSeconds: StateFlow<Double> = _currentPlayedSeconds.asStateFlow()

    private val _hasCompleted = MutableStateFlow(false)
    val hasCompleted: StateFlow<Boolean> = _hasCompleted.asStateFlow()

    var actions: List<MountAction> = emptyList()
        private set

    val totalCount: Int get() = actions.size

    private var actionDurations: MutableMap<Int, Double> = mutableMapOf()
    private var totalLessonCalorie: Double = 0.0
    private var totalLessonDuration: Double = 0.0
    private var isLoaded = false

    var onActionChanged: (() -> Unit)? = null
    var onAutoPlayComplete: (() -> Unit)? = null

    val currentAction get() = actions.getOrNull(_currentIndex.value)?.action
    val currentVideoUrl: String?
        get() = currentAction?.videos?.firstOrNull()?.videoUrl

    val totalWorkoutMinutes: Int
        get() {
            val totalSeconds = actionDurations.map { (idx, value) ->
                val maxDuration = (actions.getOrNull(idx)?.duration ?: 0).toDouble()
                minOf(value, maxDuration)
            }.sum()
            return Math.round(totalSeconds / 60.0).toInt()
        }

    val totalWorkoutCalorie: Int
        get() {
            if (totalLessonDuration <= 0) return 0
            val totalSeconds = actionDurations.map { (idx, value) ->
                val maxDuration = (actions.getOrNull(idx)?.duration ?: 0).toDouble()
                minOf(value, maxDuration)
            }.sum()
            return Math.round((totalLessonCalorie / totalLessonDuration) * totalSeconds).toInt()
        }

    fun load(mountActions: List<MountAction>, training: Training?) {
        if (isLoaded) return
        isLoaded = true
        actions = mountActions
        _currentIndex.value = 0
        actionDurations.clear()
        _hasCompleted.value = false
        training?.let {
            totalLessonCalorie = (it.calorie ?: 0).toDouble()
            totalLessonDuration = (it.duration ?: 0).toDouble()
        }
    }

    fun updatePlayedSeconds(seconds: Double) {
        val delta = seconds - _currentPlayedSeconds.value
        _currentPlayedSeconds.value = seconds
        val duration = (actions.getOrNull(_currentIndex.value)?.duration ?: 0).toDouble()
        if (duration > 0) {
            _progress.value = minOf(1.0, seconds / duration)
        }
        actionDurations[_currentIndex.value] = minOf(seconds, duration)

        if (delta > 0 && _isPlaying.value) {
            bonusElapsedSeconds += delta
            if (bonusElapsedSeconds >= bonusDurationSeconds) {
                bonusElapsedSeconds = 0.0
                // TODO: Trigger bonus reward logic
            }
            _bonusProgress.value = minOf(1.0, bonusElapsedSeconds / bonusDurationSeconds)
        }

        if (seconds >= duration && duration > 0) {
            nextAction(auto = true)
        }
    }

    fun setPlaying(playing: Boolean) {
        _isPlaying.value = playing
    }

    fun togglePlay() {
        _isPlaying.value = !_isPlaying.value
    }

    fun toggleMute() {
        _isMuted.value = !_isMuted.value
    }

    fun prevAction() {
        if (_currentIndex.value > 0) {
            _currentIndex.value -= 1
            resetForNewAction()
            onActionChanged?.invoke()
        }
    }

    fun nextAction(auto: Boolean = false) {
        if (_currentIndex.value < totalCount - 1) {
            if (auto) {
                _isPlaying.value = false
                onAutoPlayComplete?.invoke()
            } else {
                _currentIndex.value += 1
                resetForNewAction()
                onActionChanged?.invoke()
            }
        } else if (auto) {
            _hasCompleted.value = true
            _isPlaying.value = false
        }
    }

    fun continueToNextAction() {
        if (_currentIndex.value < totalCount - 1) {
            _currentIndex.value += 1
            resetForNewAction()
            onActionChanged?.invoke()
        }
    }

    private fun resetForNewAction() {
        _currentPlayedSeconds.value = 0.0
        _progress.value = 0.0
    }
}
