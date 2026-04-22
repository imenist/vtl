package com.vitalo.markrun.ui.mine

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vitalo.markrun.data.local.db.dao.RunningRecordDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MineViewModel @Inject constructor(
    private val runningRecordDao: RunningRecordDao
) : ViewModel() {

    private val _totalDistance = MutableStateFlow("0.00")
    val totalDistance: StateFlow<String> = _totalDistance.asStateFlow()

    private val _totalDuration = MutableStateFlow("0:00")
    val totalDuration: StateFlow<String> = _totalDuration.asStateFlow()

    private val _bestPace = MutableStateFlow("0'00\"")
    val bestPace: StateFlow<String> = _bestPace.asStateFlow()

    private val _totalCalories = MutableStateFlow("0")
    val totalCalories: StateFlow<String> = _totalCalories.asStateFlow()

    private val _longestDistance = MutableStateFlow("0.0")
    val longestDistance: StateFlow<String> = _longestDistance.asStateFlow()

    private val _longestDuration = MutableStateFlow("00:00")
    val longestDuration: StateFlow<String> = _longestDuration.asStateFlow()

    private val _bestPaceRecord = MutableStateFlow("0'00\"")
    val bestPaceRecord: StateFlow<String> = _bestPaceRecord.asStateFlow()

    init {
        loadStatistics()
    }

    fun loadStatistics() {
        viewModelScope.launch {
            val totalDist = runningRecordDao.getTotalDistance() ?: 0.0
            val km = totalDist / 1000.0
            _totalDistance.value = String.format("%.2f", km)

            val totalDur = runningRecordDao.getTotalDuration() ?: 0
            val hours = totalDur / 3600
            val minutes = (totalDur % 3600) / 60
            _totalDuration.value = if (hours > 0) {
                String.format("%d:%02d", hours, minutes)
            } else {
                String.format("0:%02d", minutes)
            }

            val totalCal = runningRecordDao.getTotalCalories() ?: 0.0
            _totalCalories.value = String.format("%.0f", totalCal)

            val longestDist = runningRecordDao.getLongestDistance() ?: 0.0
            _longestDistance.value = String.format("%.1f", longestDist / 1000.0)

            val longestDur = runningRecordDao.getLongestDuration() ?: 0
            val lh = longestDur / 3600
            val lm = (longestDur % 3600) / 60
            val ls = longestDur % 60
            _longestDuration.value = if (lh > 0) {
                String.format("%d:%02d", lh, lm)
            } else {
                String.format("%02d:%02d", lm, ls)
            }

            val records = runningRecordDao.getRecentRecords(1000)
            val bestPaceValue = records
                .filter { it.distance > 0 }
                .minOfOrNull { it.pace }

            if (bestPaceValue != null && bestPaceValue > 0) {
                val pMin = bestPaceValue.toInt()
                val pSec = ((bestPaceValue - pMin) * 60).toInt()
                val paceStr = "${pMin}'${String.format("%02d", pSec)}\""
                _bestPace.value = paceStr
                _bestPaceRecord.value = paceStr
            }
        }
    }
}
