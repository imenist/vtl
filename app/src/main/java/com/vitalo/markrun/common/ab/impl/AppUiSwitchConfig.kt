package com.vitalo.markrun.common.ab.impl

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import com.vitalo.markrun.common.ab.BaseAbConfig

@Keep
class AppUiSwitchConfig(
    @SerializedName("info_register_switch")
    val infoRegisterSwitch: String = "1",
    @SerializedName("daily_train_unlock_switch")
    val dailyTrainUnlockSwitch: String = "0",
    @SerializedName("train_progress_bar_switch")
    val trainProgressBarSwitch: String = "0",
    @SerializedName("wallet_page_order")
    val walletPageOrder: String = "0",
    @SerializedName("cfg_tb_id")
    val cfgTbId: Int = 0
) : BaseAbConfig()