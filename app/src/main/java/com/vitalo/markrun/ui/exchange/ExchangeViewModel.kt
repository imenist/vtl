package com.vitalo.markrun.ui.exchange

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vitalo.markrun.data.remote.model.*
import com.vitalo.markrun.data.repository.GameRepository
import com.vitalo.markrun.service.CoinManager
import com.vitalo.markrun.data.local.prefs.AppPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExchangeViewModel @Inject constructor(
    private val appPreferences: AppPreferences,
    private val coinManager: CoinManager,
    private val gameRepository: GameRepository
) : ViewModel() {

    val coinBalance: StateFlow<Double> = coinManager.coinBalance

    private val _withdrawalAmounts = MutableStateFlow<List<WithdrawalAmount>>(emptyList())
    val withdrawalAmounts: StateFlow<List<WithdrawalAmount>> = _withdrawalAmounts

    private val _withdrawalConfig = MutableStateFlow<WithdrawalConfig?>(null)
    val withdrawalConfig: StateFlow<WithdrawalConfig?> = _withdrawalConfig

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    init {
        loadCoinBalance()
        loadWithdrawalConfig()
    }

    fun loadCoinBalance() {
        coinManager.refreshCoins()
    }

    fun loadWithdrawalConfig() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val resp = gameRepository.getWithdrawalConfig()
                if (resp.isSuccess && resp.data != null) {
                    _withdrawalConfig.value = resp.data
                    _withdrawalAmounts.value = resp.data.withdrawAmounts ?: emptyList()
                }
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load withdrawal config"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun withdraw(withdrawCode: String, withdrawMethod: Int, onResult: (WithdrawalResult?) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val resp = gameRepository.withdraw(
                    withdrawCode = withdrawCode,
                    withdrawMethod = withdrawMethod
                )
                if (resp.isSuccess && resp.data != null) {
                    onResult(resp.data)
                } else {
                    _errorMessage.value = resp.errorMessage ?: "Withdrawal failed"
                    onResult(null)
                }
            } catch (e: Exception) {
                _errorMessage.value = "Network error"
                onResult(null)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }

    fun earnAdReward(rewardAmount: Int) {
        val current = appPreferences.getInt("local_coin_balance")
        appPreferences.setInt("local_coin_balance", current + rewardAmount)
        // coinManager.refreshCoins() // optionally refresh
    }
}
