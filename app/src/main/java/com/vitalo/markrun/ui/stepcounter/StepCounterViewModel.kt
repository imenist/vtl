package com.vitalo.markrun.ui.stepcounter

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.lifecycle.ViewModel
import com.vitalo.markrun.data.local.prefs.AppPreferences
import com.vitalo.markrun.data.remote.model.StepMilestone
import com.vitalo.markrun.data.remote.model.StepMilestoneStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

enum class StepPermissionStatus {
    NOT_DETERMINED, AUTHORIZED, DENIED
}

@HiltViewModel
class StepCounterViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val appPreferences: AppPreferences
) : ViewModel(), SensorEventListener {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val stepSensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

    private val _currentSteps = MutableStateFlow(0)
    val currentSteps: StateFlow<Int> = _currentSteps

    private val _permissionStatus = MutableStateFlow(
        if (stepSensor != null) StepPermissionStatus.AUTHORIZED else StepPermissionStatus.DENIED
    )
    val permissionStatus: StateFlow<StepPermissionStatus> = _permissionStatus

    private val _todayConvertedSteps = MutableStateFlow(0)
    val todayConvertedSteps: StateFlow<Int> = _todayConvertedSteps

    private val _cumulativeSteps = MutableStateFlow(0)
    val cumulativeSteps: StateFlow<Int> = _cumulativeSteps

    private val _milestones = MutableStateFlow(StepMilestone.allMilestones())
    val milestones: StateFlow<List<StepMilestone>> = _milestones

    val dailyGoal: Int
        get() = if (appPreferences.getBoolean("stepDailyGoalUpperLimit")) 15000 else 10000

    val progress: Float
        get() = if (dailyGoal > 0) minOf(_currentSteps.value.toFloat() / dailyGoal, 1f) else 0f

    private var initialStepCount = -1
    private var lastRecordedSteps = 0
    private var claimedIndices = mutableSetOf<Int>()

    init {
        refreshConvertedSteps()
        loadMilestoneData()
    }

    fun startUpdates() {
        stepSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
    }

    fun stopUpdates() {
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_STEP_COUNTER) {
            val totalSteps = event.values[0].toInt()
            if (initialStepCount < 0) {
                initialStepCount = totalSteps
            }
            val todaySteps = totalSteps - initialStepCount
            val clampedSteps = minOf(todaySteps, dailyGoal)
            _currentSteps.value = clampedSteps
            updateCumulativeSteps(clampedSteps)
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    fun refreshConvertedSteps() {
        val todayKey = todayKey()
        val savedKey = appPreferences.getString("stepConvertedDate")
        if (savedKey != todayKey) {
            appPreferences.setInt("todayStepConverted", 0)
            appPreferences.setString("stepConvertedDate", todayKey)
        }
        _todayConvertedSteps.value = appPreferences.getInt("todayStepConverted")
    }

    fun convertSteps(): Int {
        val availableSteps = minOf(_currentSteps.value, dailyGoal) - _todayConvertedSteps.value
        val stepsToConvert = (availableSteps / 10) * 10
        val coinsEarned = stepsToConvert / 10
        if (coinsEarned <= 0) return 0

        val newConverted = _todayConvertedSteps.value + stepsToConvert
        appPreferences.setInt("todayStepConverted", newConverted)
        _todayConvertedSteps.value = newConverted

        addLocalCoin(coinsEarned)
        return coinsEarned
    }

    fun getCoinsCanEarn(): Int {
        if (_permissionStatus.value != StepPermissionStatus.AUTHORIZED) return 0
        val availableSteps = minOf(_currentSteps.value, dailyGoal) - _todayConvertedSteps.value
        return (availableSteps / 10) * 10 / 10
    }

    fun getEffectiveStatus(index: Int): StepMilestoneStatus {
        if (index < 0 || index >= _milestones.value.size) return StepMilestoneStatus.LOCKED
        val milestone = _milestones.value[index]
        if (milestone.status == StepMilestoneStatus.CLAIMED) return StepMilestoneStatus.CLAIMED
        return if (_cumulativeSteps.value >= milestone.requiredSteps) StepMilestoneStatus.CLAIMABLE
        else StepMilestoneStatus.LOCKED
    }

    fun claimMilestone(index: Int) {
        if (index < 0 || index >= _milestones.value.size) return
        claimedIndices.add(index)
        val updated = _milestones.value.toMutableList()
        updated[index] = updated[index].copy(status = StepMilestoneStatus.CLAIMED)
        _milestones.value = updated
        saveMilestoneData()
        addLocalCoin(updated[index].rewardCoins)
    }

    private fun updateCumulativeSteps(newSteps: Int) {
        val delta = newSteps - lastRecordedSteps
        if (delta > 0) {
            _cumulativeSteps.value += delta
        }
        lastRecordedSteps = newSteps
        saveMilestoneData()
    }

    private fun loadMilestoneData() {
        _cumulativeSteps.value = appPreferences.getInt("stepMilestoneCumulativeSteps")
        val indicesJson = appPreferences.getString("stepMilestoneClaimedIndices")
        if (indicesJson != null) {
            try {
                claimedIndices = indicesJson.removeSurrounding("[", "]")
                    .split(",")
                    .mapNotNull { it.trim().toIntOrNull() }
                    .toMutableSet()
            } catch (_: Exception) {}
        }
        updateMilestoneStatuses()
    }

    private fun saveMilestoneData() {
        appPreferences.setInt("stepMilestoneCumulativeSteps", _cumulativeSteps.value)
        appPreferences.setString("stepMilestoneClaimedIndices", claimedIndices.toList().toString())
    }

    private fun updateMilestoneStatuses() {
        val updated = _milestones.value.mapIndexed { index, milestone ->
            if (claimedIndices.contains(index)) milestone.copy(status = StepMilestoneStatus.CLAIMED)
            else milestone
        }
        _milestones.value = updated
    }

    private fun todayKey(): String {
        val sdf = SimpleDateFormat("yyyyMMdd", Locale.US)
        return sdf.format(Date())
    }

    private fun addLocalCoin(amount: Int) {
        val current = appPreferences.getInt("local_coin_balance")
        appPreferences.setInt("local_coin_balance", current + amount)
    }

    override fun onCleared() {
        super.onCleared()
        stopUpdates()
    }
}
