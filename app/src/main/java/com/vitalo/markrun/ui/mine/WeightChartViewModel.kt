package com.vitalo.markrun.ui.mine

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vitalo.markrun.data.local.db.dao.WeightRecordDao
import com.vitalo.markrun.data.local.db.entity.WeightRecord
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class WeightChartViewModel @Inject constructor(
    private val weightRecordDao: WeightRecordDao
) : ViewModel() {

    val records: StateFlow<List<WeightRecord>> = weightRecordDao
        .getAllRecords()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _showAddDialog = MutableStateFlow(false)
    val showAddDialog: StateFlow<Boolean> = _showAddDialog.asStateFlow()

    private val _selectedRecordId = MutableStateFlow<String?>(null)
    val selectedRecordId: StateFlow<String?> = _selectedRecordId.asStateFlow()

    fun showAddDialog() {
        _showAddDialog.value = true
    }

    fun hideAddDialog() {
        _showAddDialog.value = false
    }

    fun addRecord(record: WeightRecord) {
        viewModelScope.launch {
            weightRecordDao.insert(record)
        }
    }

    fun addWeight(date: Long, weight: Int) {
        viewModelScope.launch {
            val record = WeightRecord(
                id = UUID.randomUUID().toString(),
                date = date,
                weight = weight
            )
            weightRecordDao.insert(record)
        }
    }

    fun selectRecord(id: String?) {
        _selectedRecordId.value = id
    }

    fun deleteRecord(id: String) {
        viewModelScope.launch {
            weightRecordDao.deleteById(id)
        }
    }
}
