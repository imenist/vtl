package com.vitalo.markrun.ui.exchange

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vitalo.markrun.data.remote.model.WithdrawalInfo
import com.vitalo.markrun.data.repository.GameRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WithdrawRecordViewModel @Inject constructor(
    private val gameRepository: GameRepository
) : ViewModel() {

    private val _records = MutableStateFlow<List<WithdrawalInfo>>(emptyList())
    val records: StateFlow<List<WithdrawalInfo>> = _records

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private var nextCursor: Long? = null

    fun loadRecords() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val resp = gameRepository.getWithdrawalRecords()
                if (resp.isSuccess && resp.data != null) {
                    _records.value = resp.data.withdrawInfos ?: emptyList()
                    nextCursor = resp.data.nextCursor
                }
            } catch (_: Exception) {
            } finally {
                _isLoading.value = false
            }
        }
    }
}
