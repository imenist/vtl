package com.vitalo.markrun.common.ab.impl

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import com.vitalo.markrun.common.ab.BaseAbConfig

@Keep
class AdConfig(
    @SerializedName("ad_daily_limit")
    val adDailyLimit: Int = 0,
    @SerializedName("ad_interval")
    val adInterval: Int = 0,
    @SerializedName("ad_switch_text_notes")
    val adSwitchTextNotes: String = "",
    @SerializedName("ad_name")
    val adName: String = "",
    @SerializedName("ad_reward")
    val adReward: Int = 0,
    @SerializedName("ad_switch")
    val adSwitch: String = "0",
    @SerializedName("cfg_tb_id")
    val cfgTbId: Int = 0,
    @SerializedName("ad_virtual_id")
    val virtualId: Int = 0
) : BaseAbConfig() {
    fun isOpen(): Boolean = adSwitch == "1"

    override fun toString(): String {
        return "开关:${adSwitch}=>虚拟id:${virtualId}=>间隔:${adInterval}=>次数:${adDailyLimit}"
    }
}