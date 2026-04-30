package com.vitalo.markrun.common.ab.impl

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import com.vitalo.markrun.common.ab.BaseAbConfig

@Keep
class AdPolicyConfig(
    @SerializedName("open_ad_compliance")
    val openAdCompliance: String = "1",
    @SerializedName("daily_reward_ad_limit")
    val dailyRewardAdLimit: Int = 50,
    @SerializedName("admob_daily_open_ad_limit")
    val admobDailyOpenAdLimit: Int = 3,
    @SerializedName("nofirst_cold_open_ad_switch")
    val nofirstColdOpenAdSwitch: String = "0",
    @SerializedName("hot_open_ad_switch")
    val hotOpenAdSwitch: String = "0",
    @SerializedName("reward_ad_switch")
    val rewardAdSwitch: String = "1",
    @SerializedName("daily_interstitial_ad_limit")
    val dailyInterstitialAdLimit: Int = 3,
    @SerializedName("interstitial_ad_cp_min")
    val interstitialAdCpMin: Int = 1,
    @SerializedName("cold_open_not_interstitial_min")
    val coldOpenNotInterstitialMin: Int = 2,
    @SerializedName("admob_reward_ad_limit")
    val admobRewardAdLimit: Int = 0,
    @SerializedName("daily_open_ad_limit")
    val dailyOpenAdLimit: Int = 3,
    @SerializedName("interstitial_ad_switch")
    val interstitialAdSwitch: String = "0",
    @SerializedName("first_cold_open_ad_switch")
    val firstColdOpenAdSwitch: String = "0",
    @SerializedName("admob_daily_interstitial_ad_limit")
    val admobDailyInterstitialAdLimit: Int = 0,
    @SerializedName("cfg_tb_id")
    val cfgTbId: Int = 0
) : BaseAbConfig()