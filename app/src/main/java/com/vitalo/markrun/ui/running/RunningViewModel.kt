package com.vitalo.markrun.ui.running

import android.content.Context
import android.content.Intent
import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vitalo.markrun.data.local.db.dao.RunningPointDao
import com.vitalo.markrun.data.local.db.dao.RunningRecordDao
import com.vitalo.markrun.data.local.db.entity.RunningPoint
import com.vitalo.markrun.data.local.db.entity.RunningRecord
import com.vitalo.markrun.service.LocationService
import com.vitalo.markrun.service.RunningForegroundService
import com.vitalo.markrun.service.UserManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import kotlin.math.*

enum class RunningState {
    NOT_STARTED,
    RUNNING,
    PAUSED,
    FINISHED
}

data class RunningLocationPoint(
    val latitude: Double,
    val longitude: Double,
    val timestamp: Long,
    val speed: Double
)

@HiltViewModel
class RunningViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val locationService: LocationService,
    private val runningRecordDao: RunningRecordDao,
    private val runningPointDao: RunningPointDao,
    private val userManager: UserManager
) : ViewModel() {

    private val _state = MutableStateFlow(RunningState.NOT_STARTED)
    val state: StateFlow<RunningState> = _state.asStateFlow()

    private val _locations = MutableStateFlow<List<RunningLocationPoint>>(emptyList())
    val locations: StateFlow<List<RunningLocationPoint>> = _locations.asStateFlow()

    private val _duration = MutableStateFlow(0.0)
    val duration: StateFlow<Double> = _duration.asStateFlow()

    private val _distance = MutableStateFlow(0.0)
    val distance: StateFlow<Double> = _distance.asStateFlow()

    private val _currentSpeed = MutableStateFlow(0.0)
    val currentSpeed: StateFlow<Double> = _currentSpeed.asStateFlow()

    private var startTime: Long = 0L
    private var endTime: Long = 0L
    private var timerJob: Job? = null
    private var totalPauseDuration: Long = 0L
    private var pauseStartTime: Long = 0L

    private val activeSpeedThreshold = 0.5 // m/s

    fun startRunning() {
        _state.value = RunningState.RUNNING
        _locations.value = emptyList()
        _duration.value = 0.0
        _distance.value = 0.0
        _currentSpeed.value = 0.0
        startTime = System.currentTimeMillis()
        endTime = 0L
        totalPauseDuration = 0L
        pauseStartTime = 0L

        locationService.onLocationUpdate = { location ->
            handleLocationUpdate(location)
        }
        locationService.startUpdating()
        startTimer()
        startForegroundService()
    }

    fun pauseRunning() {
        _state.value = RunningState.PAUSED
        pauseStartTime = System.currentTimeMillis()
        timerJob?.cancel()
        locationService.stopUpdating()
    }

    fun resumeRunning() {
        _state.value = RunningState.RUNNING
        if (pauseStartTime > 0) {
            totalPauseDuration += System.currentTimeMillis() - pauseStartTime
            pauseStartTime = 0L
        }
        locationService.onLocationUpdate = { location ->
            handleLocationUpdate(location)
        }
        locationService.startUpdating()
        startTimer()
    }

    fun stopRunning(onComplete: (Long?) -> Unit) {
        _state.value = RunningState.FINISHED
        endTime = System.currentTimeMillis()
        if (pauseStartTime > 0) {
            totalPauseDuration += endTime - pauseStartTime
            pauseStartTime = 0L
        }
        timerJob?.cancel()
        locationService.stopUpdating()
        locationService.onLocationUpdate = null
        stopForegroundService()

        viewModelScope.launch {
            val recordId = saveToLocalStorage()
            onComplete(recordId)
        }
    }

    fun reset() {
        _state.value = RunningState.NOT_STARTED
        _locations.value = emptyList()
        _duration.value = 0.0
        _distance.value = 0.0
        _currentSpeed.value = 0.0
        timerJob?.cancel()
        locationService.stopUpdating()
        locationService.onLocationUpdate = null
        stopForegroundService()
    }

    private fun handleLocationUpdate(location: Location) {
        val point = RunningLocationPoint(
            latitude = location.latitude,
            longitude = location.longitude,
            timestamp = System.currentTimeMillis(),
            speed = location.speed.toDouble()
        )

        val currentList = _locations.value
        if (currentList.isNotEmpty()) {
            val lastPoint = currentList.last()
            val dist = calculateDistance(
                lastPoint.latitude, lastPoint.longitude,
                point.latitude, point.longitude
            )
            // Filter out GPS noise: ignore if distance is less than 1m or more than 100m in one update
            if (dist < 1.0 || dist > 100.0) return
            _distance.value += dist
        }

        _currentSpeed.value = location.speed.toDouble()
        _locations.value = currentList + point
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                if (_state.value == RunningState.RUNNING) {
                    val elapsed = System.currentTimeMillis() - startTime - totalPauseDuration
                    _duration.value = elapsed / 1000.0
                }
            }
        }
    }

    val activeDuration: Double
        get() {
            val locs = _locations.value
            if (locs.size < 2) return 0.0
            var total = 0.0
            for (i in 0 until locs.size - 1) {
                val p0 = locs[i]
                val p1 = locs[i + 1]
                val dt = (p1.timestamp - p0.timestamp) / 1000.0
                if (dt > 0) {
                    val d = calculateDistance(p0.latitude, p0.longitude, p1.latitude, p1.longitude)
                    val speed = d / dt
                    if (speed >= activeSpeedThreshold) {
                        total += dt
                    }
                }
            }
            return total
        }

    val activePace: Double
        get() {
            val dist = _distance.value
            if (dist <= 0) return 0.0
            val minutes = activeDuration / 60.0
            val km = dist / 1000.0
            return if (km > 0) minutes / km else 0.0
        }

    val activeKcal: Double
        get() {
            val weight = userManager.currentUser?.weight?.toDouble() ?: 70.0
            val ad = activeDuration
            val dist = _distance.value
            if (ad <= 0 || dist <= 0) return 0.0
            val met = metConstant(activePace)
            val minutes = ad / 60.0
            return ((met * 3.5 * weight) / 200.0) * minutes
        }

    private fun metConstant(pace: Double): Double {
        return when {
            pace <= 0 -> 1.0
            pace < 6 -> 12.0
            pace < 7 -> 10.0
            pace < 8 -> 9.0
            pace < 9 -> 8.0
            pace < 10 -> 7.0
            pace < 12 -> 6.0
            else -> 5.0
        }
    }

    private fun calculateIntensityTimes(): Triple<Int, Int, Int> {
        val locs = _locations.value
        var low = 0
        var moderate = 0
        var high = 0
        for (i in 0 until locs.size - 1) {
            val p0 = locs[i]
            val p1 = locs[i + 1]
            val dt = ((p1.timestamp - p0.timestamp) / 1000).toInt()
            val d = calculateDistance(p0.latitude, p0.longitude, p1.latitude, p1.longitude)
            val speed = if (dt > 0) d / dt else 0.0
            when {
                speed < 1.5 -> low += dt
                speed < 3.0 -> moderate += dt
                else -> high += dt
            }
        }
        return Triple(low, moderate, high)
    }

    private suspend fun saveToLocalStorage(): Long? {
        val locs = _locations.value
        if (locs.isEmpty()) return null

        val first = locs.first()
        val last = locs.last()
        val (low, moderate, high) = calculateIntensityTimes()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        val record = RunningRecord(
            date = dateFormat.format(Date(startTime)),
            startTime = startTime / 1000,
            endTime = endTime / 1000,
            duration = _duration.value.toInt(),
            distance = _distance.value,
            speed = if (_duration.value > 0) _distance.value / _duration.value else 0.0,
            calories = activeKcal,
            startLatitude = first.latitude,
            startLongitude = first.longitude,
            endLatitude = last.latitude,
            endLongitude = last.longitude,
            lowIntensityTime = low,
            moderateIntensityTime = moderate,
            highIntensityTime = high,
            imagePath = null,
            fragmentsCount = 0,
            coins = 0,
            adCoins = null
        )

        val recordId = runningRecordDao.insert(record)

        val points = locs.map { loc ->
            RunningPoint(
                recordId = recordId,
                latitude = loc.latitude,
                longitude = loc.longitude,
                timestamp = loc.timestamp,
                speed = loc.speed
            )
        }
        runningPointDao.insertAll(points)

        return recordId
    }

    private fun startForegroundService() {
        val intent = Intent(context, RunningForegroundService::class.java).apply {
            action = RunningForegroundService.ACTION_START
        }
        context.startForegroundService(intent)
    }

    private fun stopForegroundService() {
        val intent = Intent(context, RunningForegroundService::class.java).apply {
            action = RunningForegroundService.ACTION_STOP
        }
        context.startService(intent)
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
        locationService.stopUpdating()
        locationService.onLocationUpdate = null
    }

    companion object {
        fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
            val r = 6371000.0
            val dLat = Math.toRadians(lat2 - lat1)
            val dLon = Math.toRadians(lon2 - lon1)
            val a = sin(dLat / 2).pow(2) +
                    cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                    sin(dLon / 2).pow(2)
            val c = 2 * atan2(sqrt(a), sqrt(1 - a))
            return r * c
        }
    }
}
