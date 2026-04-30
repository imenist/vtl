package com.vitalo.markrun.common.ab.impl

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import com.vitalo.markrun.common.ab.BaseAbConfig

@Keep
class FlopCoinConfig(
    @SerializedName("daily_flop_upto_1_cion")
    val dailyFlopUpto1Cion: Int = 50,
    @SerializedName("daily_flop_cion_limit")
    val dailyFlopCionLimit: Int = 2400,
    @SerializedName("every_flop_noupto_total_coin")
    val everyFlopNouptoTotalCoin: Int = 600,
    @SerializedName("cfg_tb_id")
    val cfgTbId: Int = 0
) : BaseAbConfig()