package com.vitalo.markrun.common.ab.impl

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import com.vitalo.markrun.common.ab.BaseAbConfig

@Keep
class NewUserSpinConfig(
    @SerializedName("new_user_spin_frst_coin")
    val newUserSpinFrstCoin: Int = 1000,
    @SerializedName("new_user_spin_second_coin")
    val newUserSpinSecondCoin: Int = 1000,
    @SerializedName("cfg_tb_id")
    val cfgTbId: Int = 0
) : BaseAbConfig()