package com.vitalo.markrun.common.ab.impl

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.vitalo.markrun.common.ab.BaseAbConfig

/**
 * @Date: 2026/3/20
 **/
class BonusConfig(
    @SerializedName("sce_num_json")
    val sceNumJson: String,  //1-开 0-关
) : BaseAbConfig() {

    fun getParsedSceNumJson(): SceNumJsonData? {
        if (sceNumJson.isBlank()) return null
        return try {
            Gson().fromJson(sceNumJson, SceNumJsonData::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}

data class SceNumJsonData(
    @SerializedName("global")
    val global: GlobalConfig? = null,
    @SerializedName("f_factor_rules")
    val fFactorRules: List<FFactorRule>? = null,
    @SerializedName("base_rewards")
    val baseRewards: BaseRewardsConfig? = null
)

data class GlobalConfig(
    @SerializedName("target_amount")
    val targetAmount: Double? = null,
    @SerializedName("ad_multiplier")
    val adMultiplier: Double? = null
)

data class FFactorRule(
    @SerializedName("gap_percent_min")
    val gapPercentMin: Double? = null,
    @SerializedName("f_min")
    val fMin: Double? = null,
    @SerializedName("f_max")
    val fMax: Double? = null,
    @SerializedName("f_min_formula")
    val fMinFormula: String? = null,
    @SerializedName("f_max_formula")
    val fMaxFormula: String? = null
)

data class BaseRewardsConfig(
    @SerializedName("bubble")
    val bubble: Double? = null,
    @SerializedName("watch_time")
    val watchTime: List<WatchTimeReward>? = null,
    @SerializedName("watch_episodes")
    val watchEpisodes: List<WatchEpisodeReward>? = null,
    @SerializedName("sign_in")
    val signIn: List<SignInReward>? = null,
    @SerializedName("direct_ad")
    val directAd: DirectAdReward? = null
)

data class WatchTimeReward(
    @SerializedName("minutes")
    val minutes: Int? = null,
    @SerializedName("reward")
    val reward: Double? = null
)

data class WatchEpisodeReward(
    @SerializedName("episodes")
    val episodes: Int? = null,
    @SerializedName("reward")
    val reward: Double? = null
)

data class SignInReward(
    @SerializedName("day")
    val day: Int? = null,
    @SerializedName("reward")
    val reward: Double? = null
)

data class DirectAdReward(
    @SerializedName("reward")
    val reward: Double? = null,
    @SerializedName("daily_limit")
    val dailyLimit: Int? = null
)