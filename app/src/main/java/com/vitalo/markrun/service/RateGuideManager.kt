package com.vitalo.markrun.service

import com.vitalo.markrun.common.ab.AbConfigDataRepo
import com.vitalo.markrun.common.ab.AbSidTable
import com.vitalo.markrun.common.ab.impl.DailyGuideConfig
import com.vitalo.markrun.util.MmkvUtils

object RateGuideManager {
    enum class Scene {
        SIGN, SMALL_MONEY, BIG_MONEY, SETTINGS
    }

    private var hasShownInCurrentSession = false

    fun checkAndShow(scene: Scene): Boolean {
        if (hasShownInCurrentSession) return false

        if (MmkvUtils.getBoolean("has_clicked_good_rate", false)) return false

        if (!isSceneEnabled(scene)) return false

        val currentCount = MmkvUtils.getInt("good_rate_show_count", 0)
        val limit = getLimit()
        if (currentCount >= limit) return false

        return true
    }

    fun onRateGuideShown() {
        hasShownInCurrentSession = true
        val currentCount = MmkvUtils.getInt("good_rate_show_count", 0)
        MmkvUtils.putInt("good_rate_show_count", currentCount + 1)
    }

    fun onRateLikeClicked() {
        MmkvUtils.putBoolean("has_clicked_good_rate", true)
    }

    private fun isSceneEnabled(scene: Scene): Boolean {
        val cfg = AbConfigDataRepo.getCurrentConfig(AbSidTable.DAILY_GUIDE) as? DailyGuideConfig ?: return false
        
        return when (scene) {
            Scene.SIGN -> cfg.signGoodRateSwitch == "1"
            Scene.SMALL_MONEY -> cfg.smallMoneyGoodRateSwitch == "1"
            Scene.BIG_MONEY -> cfg.bigMoneyGoodRateSwitch == "1"
            Scene.SETTINGS -> true
        }
    }

    private fun getLimit(): Int {
        val cfg = AbConfigDataRepo.getCurrentConfig(AbSidTable.DAILY_GUIDE) as? DailyGuideConfig ?: return 0
        return cfg.goodRateLimit
    }
}
