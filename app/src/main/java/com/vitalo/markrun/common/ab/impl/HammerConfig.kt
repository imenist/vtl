package com.vitalo.markrun.common.ab.impl

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import com.vitalo.markrun.common.ab.BaseAbConfig

@Keep
class HammerConfig(
    @SerializedName("daily_hammer_limit")
    val dailyHammerLimit: Int = 15,
    @SerializedName("daily_free_hammer")
    val dailyFreeHammer: Int = 3,
    @SerializedName("cfg_tb_id")
    val cfgTbId: Int = 0
) : BaseAbConfig()