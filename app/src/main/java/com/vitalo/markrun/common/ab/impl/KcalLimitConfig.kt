package com.vitalo.markrun.common.ab.impl

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import com.vitalo.markrun.common.ab.BaseAbConfig

@Keep
class KcalLimitConfig(
    @SerializedName("1_point_kcal_number")
    val pointKcalNumber: Int = 1,
    @SerializedName("daily_training_kcal_limit")
    val dailyTrainingKcalLimit: Int = 50,
    @SerializedName("daily_runing_kcal_limit")
    val dailyRuningKcalLimit: Int = 100,
    @SerializedName("cfg_tb_id")
    val cfgTbId: Int = 0
) : BaseAbConfig()