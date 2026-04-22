package com.vitalo.markrun.ui.common

import androidx.lifecycle.ViewModel
import com.vitalo.markrun.service.CoinManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class CoinBalanceViewModel @Inject constructor(
    private val coinManager: CoinManager
) : ViewModel() {

    val coinBalance: StateFlow<Double> = coinManager.coinBalance

    fun refresh() {
        coinManager.refreshCoins()
    }
}
