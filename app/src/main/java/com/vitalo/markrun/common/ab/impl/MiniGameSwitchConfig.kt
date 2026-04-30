package com.vitalo.markrun.common.ab.impl

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import com.vitalo.markrun.common.ab.BaseAbConfig

@Keep
class MiniGameSwitchConfig(
    @SerializedName("h5_switch_text_notes")
    val h5SwitchTextNotes: String = "",
    @SerializedName("new_user_spin_switch")
    val newUserSpinSwitch: String = "0",
    @SerializedName("golden_egg_switch")
    val goldenEggSwitch: String = "0",
    @SerializedName("daily_sign_switch")
    val dailySignSwitch: String = "1",
    @SerializedName("flop_switch")
    val flopSwitch: String = "0",
    @SerializedName("slot_switch")
    val slotSwitch: String = "0",
    @SerializedName("cfg_tb_id")
    val cfgTbId: Int = 0
) : BaseAbConfig()