package com.vitalo.markrun.common.ab.impl

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import com.vitalo.markrun.common.ab.BaseAbConfig

@Keep
class SlotConfig(
    @SerializedName("slot_daily_limit")
    val slotDailyLimit: Int = 15,
    @SerializedName("slot_daily_free_chance")
    val slotDailyFreeChance: Int = 3,
    @SerializedName("cfg_tb_id")
    val cfgTbId: Int = 0
) : BaseAbConfig()