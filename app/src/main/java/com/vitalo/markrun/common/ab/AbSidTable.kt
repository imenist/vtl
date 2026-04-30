package com.vitalo.markrun.common.ab

import com.vitalo.markrun.common.ab.impl.*

/**
 * AB 实验 SID 注册表（与 iOS `DefaultABTestConfig` / `BuyUserABTestConfig` 中 `infos_<sid>` 一致）。
 *
 * 新增实验：在此增加 [AbConfigContract]，并在 [AbManager] 的 contracts 列表中注册。
 */
object AbSidTable {
    val KCAL_LIMIT = AbConfigContract(1801, KcalLimitConfig::class)
    val NEW_USER_SPIN = AbConfigContract(1803, NewUserSpinConfig::class)
    val SIGN_REWARD = AbConfigContract(1805, SignRewardConfig::class)
    val HAMMER = AbConfigContract(1807, HammerConfig::class)
    val SLOT_MACHINE = AbConfigContract(1809, SlotConfig::class)
    val FLOP_COIN = AbConfigContract(1811, FlopCoinConfig::class)
    val TASK_REWARD = AbConfigContract(1813, TaskRewardConfig::class)
    val MINI_GAME_SWITCH = AbConfigContract(1815, MiniGameSwitchConfig::class)
    val DAILY_GUIDE = AbConfigContract(1829, DailyGuideConfig::class)
    val AD = AbConfigContract(1831, AdConfig::class)
    val AD_POLICY = AbConfigContract(1833, AdPolicyConfig::class)
    val WITHDRAW_ENABLE = AbConfigContract(1837, WithdrawEnableConfig::class)
    val WITHDRAW_GRADE = AbConfigContract(1839, WithdrawGradeConfig::class)
    val H5_TASK_AD = AbConfigContract(1847, H5TaskAdConfig::class)
    val APP_UI_SWITCH = AbConfigContract(1851, AppUiSwitchConfig::class)
    
    // 旧的实验
    val WITHDRAW = AbConfigContract(1933, WithDrawConfig::class)
    val BONUS = AbConfigContract(1935, BonusConfig::class)
}