package com.vitalo.markrun.service

import com.vitalo.markrun.common.ab.AbConfigDataRepo
import com.vitalo.markrun.common.ab.AbSidTable
import com.vitalo.markrun.common.ab.impl.DailyGuideConfig
import com.vitalo.markrun.common.ab.impl.MiniGameSwitchConfig
import com.vitalo.markrun.util.MmkvUtils
import com.vitalo.markrun.ad.AdManager
import com.vitalo.markrun.ad.Ads
import java.util.Calendar

/**
 * 对应 iOS UserGuideManager
 * 处理各类强弹引导的展示逻辑与小游戏开关判断
 */
object UserGuideManager {

    private var hasCheckedRedeemGuideThisSession = false

    private fun getWebSwitchCfg(): MiniGameSwitchConfig? {
        return AbConfigDataRepo.getCurrentConfig(AbSidTable.MINI_GAME_SWITCH) as? MiniGameSwitchConfig
    }

    fun getUserGuideCfg(): DailyGuideConfig? {
        return AbConfigDataRepo.getCurrentConfig(AbSidTable.DAILY_GUIDE) as? DailyGuideConfig
    }

    private fun isSameDay(timestamp: Long): Boolean {
        if (timestamp <= 0) return false
        val cal1 = Calendar.getInstance().apply { timeInMillis = timestamp }
        val cal2 = Calendar.getInstance()
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
               cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }

    // MARK: - 转盘引导相关
    fun shouldShowWheelGuide(): Boolean {
        val userGuideCfg = getUserGuideCfg()
        if (userGuideCfg?.dailyGuideSwitch != "1") return false

        val webSwitchCfg = getWebSwitchCfg()
        if (webSwitchCfg?.newUserSpinSwitch != "1") return false

        if (!AdManager.isAdAvailable(Ads.REWARD_ACTIVITY_LUCKY_WHEEL)) return false

        val todayCount = getTodayCount("wheel_guide")
        if (todayCount >= (userGuideCfg.dailyGuideWheelLimit)) return false

        return true
    }

    fun checkAndShowWheelGuide(): Boolean {
        if (shouldShowWheelGuide()) {
            recordShown("wheel_guide")
            return true
        }
        return false
    }

    // MARK: - 签到引导相关
    fun shouldShowCheckinGuide(): Boolean {
        val userGuideCfg = getUserGuideCfg()
        if (userGuideCfg?.dailyGuideSwitch != "1") return false

        val webSwitchCfg = getWebSwitchCfg()
        if (webSwitchCfg?.dailySignSwitch != "1") return false

        if (!AdManager.isAdAvailable(Ads.REWARD_ACTIVITY_SIGN_IN)) return false

        val todayCount = getTodayCount("checkin_guide")
        if (todayCount >= (userGuideCfg.dailyGuideCheckinLimit)) return false

        return true
    }

    fun checkAndShowCheckinGuide(): Boolean {
        if (shouldShowCheckinGuide()) {
            recordShown("checkin_guide")
            return true
        }
        return false
    }

    // MARK: - 砸蛋引导相关
    fun shouldShowEggGuide(): Boolean {
        val userGuideCfg = getUserGuideCfg()
        if (userGuideCfg?.dailyGuideSwitch != "1") return false

        val webSwitchCfg = getWebSwitchCfg()
        if (webSwitchCfg?.goldenEggSwitch != "1") return false

        if (!AdManager.isAdAvailable(Ads.REWARD_ACTIVITY_SMASH_EGG)) return false

        val todayCount = getTodayCount("egg_guide")
        if (todayCount >= (userGuideCfg.dailyGuideEggLimit)) return false

        return true
    }

    fun checkAndShowEggGuide(): Boolean {
        if (shouldShowEggGuide()) {
            recordShown("egg_guide")
            return true
        }
        return false
    }

    // MARK: - 兑换引导相关
    fun shouldShowRedeemGuide(): Boolean {
        val userGuideCfg = getUserGuideCfg()
        if (userGuideCfg?.dailyGuideSwitch != "1") return false

        val todayCount = getTodayCount("redeem_guide")
        if (todayCount >= (userGuideCfg.dailyGuideRedeemLimit)) return false

        return true
    }

    fun checkAndShowRedeemGuide(): Boolean {
        if (hasCheckedRedeemGuideThisSession) return false
        
        if (shouldShowRedeemGuide()) {
            recordShown("redeem_guide")
            hasCheckedRedeemGuideThisSession = true
            return true
        }
        
        hasCheckedRedeemGuideThisSession = true
        return false
    }

    // MARK: - 最小兑换引导相关
    fun shouldShowMinRedeemGuide(): Boolean {
        val userGuideCfg = getUserGuideCfg()
        if (userGuideCfg?.dailyGuideSwitch != "1") return false

        val todayCount = getTodayCount("minredeem_guide")
        if (todayCount >= (userGuideCfg.dailyGuideMinredeemLimit)) return false

        return true
    }

    fun recordMinRedeemGuideShown() {
        recordShown("minredeem_guide")
    }

    // MARK: - 最大兑换引导相关
    fun shouldShowMaxRedeemGuide(): Boolean {
        val userGuideCfg = getUserGuideCfg()
        if (userGuideCfg?.dailyGuideSwitch != "1") return false

        val todayCount = getTodayCount("maxredeem_guide")
        if (todayCount >= (userGuideCfg.dailyGuideMaxredeemLimit)) return false

        return true
    }

    fun recordMaxRedeemGuideShown() {
        recordShown("maxredeem_guide")
    }

    // MARK: - 任务引导相关
    fun shouldShowTaskGuide(): Boolean {
        val userGuideCfg = getUserGuideCfg()
        if (userGuideCfg?.dailyGuideSwitch != "1") return false

        val todayCount = getTodayCount("task_guide")
        if (todayCount >= (userGuideCfg.dailyGuideTaskLimit)) return false

        return true
    }

    fun checkAndShowTaskGuide(): Boolean {
        if (shouldShowTaskGuide()) {
            recordShown("task_guide")
            return true
        }
        return false
    }

    // MARK: - 翻牌卡触发判断
    fun canTriggerFlipCard(): Boolean {
        val webSwitchCfg = getWebSwitchCfg()
        if (webSwitchCfg?.flopSwitch != "1") {
            return false
        }
        if (!AdManager.isAdAvailable(Ads.REWARD_ACTIVITY_FLIP_CARD)) {
            return false
        }
        return true
    }

    // MARK: - 计数辅助方法
    private fun getTodayCount(key: String): Int {
        val lastDate = MmkvUtils.getLong("guide_last_date_$key", 0L)
        if (!isSameDay(lastDate)) {
            MmkvUtils.putInt("guide_count_$key", 0)
            return 0
        }
        return MmkvUtils.getInt("guide_count_$key", 0)
    }

    private fun recordShown(key: String) {
        val lastDate = MmkvUtils.getLong("guide_last_date_$key", 0L)
        val now = System.currentTimeMillis()
        if (!isSameDay(lastDate)) {
            MmkvUtils.putInt("guide_count_$key", 1)
        } else {
            val count = MmkvUtils.getInt("guide_count_$key", 0)
            MmkvUtils.putInt("guide_count_$key", count + 1)
        }
        MmkvUtils.putLong("guide_last_date_$key", now)
    }
}
