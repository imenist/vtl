package com.vitalo.markrun.service

import com.vitalo.markrun.data.local.prefs.AppPreferences
import com.vitalo.markrun.data.repository.CoinRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CoinManager @Inject constructor(
    private val appPreferences: AppPreferences,
    private val coinRepository: CoinRepository
) {
    private val _coinBalance = MutableStateFlow(0.0)
    val coinBalance: StateFlow<Double> = _coinBalance

    private val scope = CoroutineScope(Dispatchers.IO)

    init {
        val localCoin = appPreferences.getInt("local_coin_balance").toDouble()
        _coinBalance.value = localCoin
        refreshCoins()
    }

    fun refreshCoins() {
        scope.launch {
            try {
                val result = coinRepository.getCoinInfos()
                if (result.isSuccess && result.data != null) {
                    val usdCoin = result.data.coinsInfo.firstOrNull { it.coinCode == "COIN_USD" }
                    if (usdCoin != null) {
                        _coinBalance.value = usdCoin.existingCoin
                        // Optionally update local fallback cache
                        appPreferences.setInt("local_coin_balance", usdCoin.existingCoin.toInt())
                    }
                } else {
                    _coinBalance.value = appPreferences.getInt("local_coin_balance").toDouble()
                }
            } catch (_: Exception) {
                _coinBalance.value = appPreferences.getInt("local_coin_balance").toDouble()
            }
        }
    }
}
