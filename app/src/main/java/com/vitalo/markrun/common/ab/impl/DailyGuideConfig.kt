package com.vitalo.markrun.common.ab.impl

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import com.vitalo.markrun.common.ab.BaseAbConfig

@Keep
class DailyGuideConfig(
    @SerializedName("daily_guide_switch")
    val dailyGuideSwitch: String = "0",
    @SerializedName("daily_guide_task_limit")
    val dailyGuideTaskLimit: Int = 3,
    @SerializedName("daily_guide_checkin_limit")
    val dailyGuideCheckinLimit: Int = 3,
    @SerializedName("daily_guide_wheel_limit")
    val dailyGuideWheelLimit: Int = 5,
    @SerializedName("daily_guide_h5_limit")
    val dailyGuideH5Limit: Int = 15,
    @SerializedName("daily_guide_redeem_limit")
    val dailyGuideRedeemLimit: Int = 3,
    @SerializedName("daily_guide_egg_limit")
    val dailyGuideEggLimit: Int = 3,
    @SerializedName("daily_guide_minredeem_limit")
    val dailyGuideMinredeemLimit: Int = 3,
    @SerializedName("daily_guide_maxredeem_limit")
    val dailyGuideMaxredeemLimit: Int = 3,
    @SerializedName("sign_good_rate_switch")
    val signGoodRateSwitch: String = "0",
    @SerializedName("small_money_good_rate_switch")
    val smallMoneyGoodRateSwitch: String = "0",
    @SerializedName("big_money_good_rate_switch")
    val bigMoneyGoodRateSwitch: String = "0",
    @SerializedName("good_rate_limit")
    val goodRateLimit: Int = 0,
    @SerializedName("cfg_tb_id")
    val cfgTbId: Int = 0
) : BaseAbConfig()