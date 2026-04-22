package com.vitalo.markrun.ui.recentactivities

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vitalo.markrun.data.local.db.dao.RunningRecordDao
import com.vitalo.markrun.data.local.db.entity.RunningRecord
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecentActivitiesViewModel @Inject constructor(
    private val runningRecordDao: RunningRecordDao
) : ViewModel() {

    private val _records = MutableStateFlow<List<RunningRecord>>(emptyList())
    val records: StateFlow<List<RunningRecord>> = _records.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadRecords()
    }

    private fun loadRecords() {
        viewModelScope.launch {
            _isLoading.value = true
            runningRecordDao.getAllRecords().collect { allRecords ->
                _records.value = allRecords
                _isLoading.value = false
            }
        }
    }
}
