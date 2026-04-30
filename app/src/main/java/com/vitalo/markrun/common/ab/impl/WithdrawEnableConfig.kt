package com.vitalo.markrun.common.ab.impl

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import com.vitalo.markrun.common.ab.BaseAbConfig

@Keep
class WithdrawEnableConfig(
    @SerializedName("small_withdraw_enable")
    val smallWithdrawEnable: String = "0",
    @SerializedName("large_withdraw_enable")
    val largeWithdrawEnable: String = "0",
    @SerializedName("1_usd_coin_number")
    val usd1CoinNumber: Int = 1000,
    @SerializedName("cfg_tb_id")
    val cfgTbId: Int = 0
) : BaseAbConfig()