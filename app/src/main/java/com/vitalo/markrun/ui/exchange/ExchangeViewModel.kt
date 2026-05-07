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
import kotlinx.coroutines.isActive
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

    private val _shouldShowCountdown = MutableStateFlow(true)
    val shouldShowCountdown: StateFlow<Boolean> = _shouldShowCountdown

    private val _countdownText = MutableStateFlow("00:00:00")
    val countdownText: StateFlow<String> = _countdownText

    private var countdownJob: kotlinx.coroutines.Job? = null

    init {
        loadCoinBalance()
        loadWithdrawalConfig()
        startRegistrationTimer()
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
                    
                    val abConfig = com.vitalo.markrun.common.ab.AbConfigDataRepo.getCurrentConfig(com.vitalo.markrun.common.ab.AbSidTable.WITHDRAW_ENABLE) as? com.vitalo.markrun.common.ab.impl.WithdrawEnableConfig
                    val smallEnabled = abConfig?.smallWithdrawEnable == "1"
                    val largeEnabled = abConfig?.largeWithdrawEnable == "1"

                    val withdrawGradeConfig = getFrozenWithdrawGradeConfig()
                    val noAdCode = withdrawGradeConfig?.noAd1GradeCode?.trim()
                    val twoCode = withdrawGradeConfig?.grade2Code?.trim()
                    val twoNeedAds = withdrawGradeConfig?.grade2NeedAds ?: 0

                    val allAmounts = resp.data.withdrawAmounts ?: emptyList()
                    val arr = mutableListOf<WithdrawalAmount>()

                    // Small amounts logic
                    if (smallEnabled) {
                        if (!noAdCode.isNullOrEmpty() && noAdCode != "0") {
                            val item = allAmounts.firstOrNull { it.withdrawCode == noAdCode }
                            if (item != null) {
                                arr.add(item)
                            }
                        } else if (!twoCode.isNullOrEmpty() && twoCode != "0") {
                            val item = allAmounts.firstOrNull { it.withdrawCode == twoCode }
                            if (item != null) {
                                val modifiedItem = item.copy()
                                modifiedItem.watchAdTimes = twoNeedAds
                                arr.add(modifiedItem)
                            }
                        }
                    }

                    // Large amounts logic
                    if (largeEnabled) {
                        val targets = listOf(30.0, 50.0, 100.0, 150.0)
                        val coinExchangeRate = 1000.0 // TODO: get from ABTestResultManager
                        for (target in targets) {
                            val item = allAmounts.firstOrNull { it.realCurrency == target }
                            if (item != null) {
                                val modifiedItem = item.copy()
                                modifiedItem.coinAmountV2 = target * coinExchangeRate
                                when (target) {
                                    30.0 -> modifiedItem.crackEggFragments = 60
                                    50.0 -> {
                                        modifiedItem.limitSignDays = 14
                                        modifiedItem.crackEggFragments = 120
                                    }
                                    100.0 -> {
                                        modifiedItem.limitSignDays = 21
                                        modifiedItem.crackEggFragments = 300
                                    }
                                    150.0 -> {
                                        modifiedItem.limitSignDays = 28
                                        modifiedItem.crackEggFragments = 600
                                    }
                                }
                                // TODO: check if completed
                                arr.add(modifiedItem)
                            }
                        }
                    }

                    _withdrawalAmounts.value = arr
                }
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load withdrawal config"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun getFrozenWithdrawGradeConfig(): com.vitalo.markrun.common.ab.impl.WithdrawGradeConfig? {
        val cachedJson = appPreferences.getString("FrozenWithdrawGradeConfig")
        if (!cachedJson.isNullOrEmpty()) {
            return com.vitalo.markrun.util.MmkvUtils.gson.fromJson(cachedJson, com.vitalo.markrun.common.ab.impl.WithdrawGradeConfig::class.java)
        }
        val config = com.vitalo.markrun.common.ab.AbConfigDataRepo.getCurrentConfig(com.vitalo.markrun.common.ab.AbSidTable.WITHDRAW_GRADE) as? com.vitalo.markrun.common.ab.impl.WithdrawGradeConfig
        if (config != null) {
            appPreferences.setString("FrozenWithdrawGradeConfig", com.vitalo.markrun.util.MmkvUtils.gson.toJson(config))
        }
        return config
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

    private fun startRegistrationTimer() {
        countdownJob?.cancel()

        val hasEverShownReviewDialog = appPreferences.getBoolean(AppPreferences.KEY_WITHDRAW_REVIEW_DIALOG_EVER_SHOWN)
        if (hasEverShownReviewDialog) {
            _shouldShowCountdown.value = false
            return
        }

        _shouldShowCountdown.value = true
        updateRegistrationCountdown()

        if (isWithin24HoursOfRegistration()) {
            countdownJob = viewModelScope.launch {
                while (isActive) {
                    kotlinx.coroutines.delay(1000)
                    updateRegistrationCountdown()
                }
            }
        }
    }

    private fun isWithin24HoursOfRegistration(): Boolean {
        val installTimestamp = appPreferences.getInstallDate()
        if (installTimestamp <= 0) return false
        val now = System.currentTimeMillis()
        val remaining = (installTimestamp + 24 * 3600 * 1000) - now
        return remaining > 0
    }

    private fun updateRegistrationCountdown() {
        val installTimestamp = appPreferences.getInstallDate()
        if (installTimestamp <= 0) {
            _countdownText.value = "00:00:00"
            return
        }

        val endDate = installTimestamp + 24 * 3600 * 1000
        val now = System.currentTimeMillis()
        val remaining = kotlin.math.max(0L, (endDate - now) / 1000)

        if (remaining > 0) {
            val hours = remaining / 3600
            val minutes = (remaining % 3600) / 60
            val seconds = remaining % 60
            _countdownText.value = String.format(java.util.Locale.US, "%02d:%02d:%02d", hours, minutes, seconds)
        } else {
            _countdownText.value = "00:00:00"
            countdownJob?.cancel()
            loadWithdrawalConfig()
        }
    }
}
