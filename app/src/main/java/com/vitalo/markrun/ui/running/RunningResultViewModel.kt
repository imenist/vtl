package com.vitalo.markrun.ui.running

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vitalo.markrun.data.local.db.dao.RunningPointDao
import com.vitalo.markrun.data.local.db.dao.RunningRecordDao
import com.vitalo.markrun.data.local.db.entity.RunningPoint
import com.vitalo.markrun.data.local.db.entity.RunningRecord
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RunningResultViewModel @Inject constructor(
    private val runningRecordDao: RunningRecordDao,
    private val runningPointDao: RunningPointDao
) : ViewModel() {

    private val _record = MutableStateFlow<RunningRecord?>(null)
    val record: StateFlow<RunningRecord?> = _record.asStateFlow()

    private val _routePoints = MutableStateFlow<List<RunningPoint>>(emptyList())
    val routePoints: StateFlow<List<RunningPoint>> = _routePoints.asStateFlow()

    fun loadRecord(recordId: Long) {
        viewModelScope.launch {
            _record.value = runningRecordDao.getById(recordId)
            _routePoints.value = runningPointDao.getPointsByRecordId(recordId)
        }
    }

    fun deleteRecord(recordId: Long) {
        viewModelScope.launch {
            runningRecordDao.deleteById(recordId)
        }
    }
}
