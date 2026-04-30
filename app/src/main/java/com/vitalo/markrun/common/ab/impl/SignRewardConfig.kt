package com.vitalo.markrun.common.ab.impl

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import com.vitalo.markrun.common.ab.BaseAbConfig

@Keep
class SignRewardConfig(
    @SerializedName("sign_1_coin")
    val sign1Coin: Int = 100,
    @SerializedName("sign_7_coin")
    val sign7Coin: Int = 200,
    @SerializedName("sign_streak_coin")
    val signStreakCoin: Int = 300,
    @SerializedName("sign_cash_day")
    val signCashDay: String = "",
    @SerializedName("cfg_tb_id")
    val cfgTbId: Int = 0
) : BaseAbConfig()