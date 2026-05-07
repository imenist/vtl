package com.vitalo.markrun.ui.followalong

import androidx.lifecycle.ViewModel
import com.vitalo.markrun.ad.AdManager
import com.vitalo.markrun.common.ab.AbConfigDataRepo
import com.vitalo.markrun.common.ab.AbSidTable
import com.vitalo.markrun.common.ab.impl.WithdrawEnableConfig
import com.vitalo.markrun.service.CoinManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class WorkoutResultViewModel @Inject constructor(
    private val coinManager: CoinManager
) : ViewModel() {

    fun isAdAvailable(virtualId: Int): Boolean {
        return AdManager.isAdAvailable(virtualId)
    }

    fun getAdReward(virtualId: Int): Int {
        val config = AdManager.getAdConfig(virtualId)
        return config?.adReward ?: 0
    }

    fun isLargeWithdrawEnable(): Boolean {
        val config = AbConfigDataRepo.getCurrentConfig(AbSidTable.WITHDRAW_ENABLE) as? WithdrawEnableConfig
        return config?.largeWithdrawEnable == "1"
    }

    fun addCoins(amount: Int) {
        coinManager.addCoin(amount)
    }
}
