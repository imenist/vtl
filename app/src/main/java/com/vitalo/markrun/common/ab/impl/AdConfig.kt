package com.vitalo.markrun.common.ab.impl

import com.google.gson.annotations.SerializedName
import com.vitalo.markrun.common.ab.BaseAbConfig

class AdConfig(
    @SerializedName("ads_switch")
    val adSwitch: String = "0",  //1-开 0-关
    @SerializedName("ads_virtual_id")
    val virtualId: Int = -1,
    @SerializedName("ads_lag")
    val adIntervals: Int = 0,
    @SerializedName("ads_time")
    val adNumLimit: Int = 999,
): BaseAbConfig() {

    override fun toString(): String {
        return "开关:${adSwitch}=>虚拟id:${virtualId}=>间隔:${adIntervals}=>次数:${adNumLimit}"
    }

    fun isOpen(): Boolean {
        return adSwitch == "1"
    }
}