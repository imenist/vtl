package com.vitalo.markrun.common.ab.impl

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import com.vitalo.markrun.common.ab.BaseAbConfig

@Keep
class TaskRewardConfig(
    @SerializedName("new_user_spin_10_rewards")
    val newUserSpin10Rewards: Int = 1000,
    @SerializedName("today_sign_in_rewards")
    val todaySignInRewards: Int = 120,
    @SerializedName("limited_time_chest_rewards")
    val limitedTimeChestRewards: Int = 200,
    @SerializedName("crack_egg_10_rewards")
    val crackEgg10Rewards: Int = 1500,
    @SerializedName("training_30_min_rewards")
    val training30MinRewards: Int = 150,
    @SerializedName("up_run_kacl_limit_rewards")
    val upRunKaclLimitRewards: Int = 50,
    @SerializedName("lucky_slot_10_rewards")
    val luckySlot10Rewards: Int = 1200,
    @SerializedName("limited_time_chest_4_rewards")
    val limitedTimeChest4Rewards: Int = 1000,
    @SerializedName("run_200_kcal_rewards")
    val run200KcalRewards: Int = 150,
    @SerializedName("ask_ad_read_rewards")
    val askAdReadRewards: Int = 120,
    @SerializedName("daily_consult_ad_rewards")
    val dailyConsultAdRewards: Int = 100,
    @SerializedName("health_permissions_enable")
    val healthPermissionsEnable: Int = 200,
    @SerializedName("cfg_tb_id")
    val cfgTbId: Int = 0
) : BaseAbConfig()