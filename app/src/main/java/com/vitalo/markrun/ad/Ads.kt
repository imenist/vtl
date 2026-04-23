package com.vitalo.markrun.ad

/**
 * @Date: 2026/4/23
 **/
object Ads {
    // 测试：正式 (目前只配置测试服，所以两个都使用同一个虚拟id)

    // 开屏广告 (Splash)
    private val SPLASH_FIRST_COLD_START_ID = Pair(10001381, 10001381)
    private val SPLASH_NON_FIRST_COLD_START_ID = Pair(10001379, 10001379)
    private val SPLASH_HOT_START_ID = Pair(10001379, 10001379)

    // 插屏广告 (Interstitial)
    private val INTERSTITIAL_COURSE_END_ID = Pair(10001375, 10001375)
    private val INTERSTITIAL_RUN_END_ID = Pair(10001373, 10001373)
    private val INTERSTITIAL_TASK_REWARD_POPUP_ID = Pair(10001369, 10001369)
    private val INTERSTITIAL_NON_GUIDE_H5_ID = Pair(10001367, 10001367)

    // 激励广告 (Reward)
    private val REWARD_TASK_MULTI_BROWSE_ID = Pair(10001389, 10001389)
    private val REWARD_HOME_STEP_ID = Pair(10001387, 10001387)
    private val REWARD_TASK_STEP_LIMIT_UP_ID = Pair(10001385, 10001385)
    private val REWARD_SMALL_WITHDRAW_ID = Pair(10001371, 10001371)
    private val REWARD_GLOBAL_FLOAT_ID = Pair(10001365, 10001365)
    private val REWARD_HOME_FIXED_WEB_ID = Pair(10001363, 10001363)
    private val REWARD_WITHDRAW_PAGE_ID = Pair(10001361, 10001361)
    private val REWARD_COURSE_REST_ID = Pair(10001359, 10001359)
    private val REWARD_COURSE_MID_END_ID = Pair(10001357, 10001357)
    private val REWARD_COURSE_FINISH_ID = Pair(10001355, 10001355)
    private val REWARD_EVENT_POINT_1_ID = Pair(10001353, 10001353)
    private val REWARD_EVENT_POINT_2_ID = Pair(10001351, 10001351)
    private val REWARD_RUN_END_ID = Pair(10001349, 10001349)
    private val REWARD_TASK_LIMIT_UP_ID = Pair(10001347, 10001347)
    private val REWARD_ACTIVITY_SIGN_IN_ID = Pair(10001345, 10001345)
    private val REWARD_ACTIVITY_LUCKY_WHEEL_ID = Pair(10001343, 10001343)
    private val REWARD_ACTIVITY_SMASH_EGG_ID = Pair(10001341, 10001341)
    private val REWARD_ACTIVITY_SLOT_MACHINE_ID = Pair(10001339, 10001339)
    private val REWARD_ACTIVITY_FLIP_CARD_ID = Pair(10001337, 10001337)

    // 任务界面 (Task)
    private val TASK_BROWSE_ID = Pair(10001383, 10001383)

    /*------- 开屏广告  -------*/
    /**
     * 首次冷启动
     */
    val SPLASH_FIRST_COLD_START: Int by lazy { SPLASH_FIRST_COLD_START_ID.virtualId() }
    /**
     * 非首次冷启动
     */
    val SPLASH_NON_FIRST_COLD_START: Int by lazy { SPLASH_NON_FIRST_COLD_START_ID.virtualId() }
    /**
     * 热启动
     */
    val SPLASH_HOT_START: Int by lazy { SPLASH_HOT_START_ID.virtualId() }

    /*------- 插屏广告  -------*/
    /**
     * 插屏-课程结束界面
     */
    val INTERSTITIAL_COURSE_END: Int by lazy { INTERSTITIAL_COURSE_END_ID.virtualId() }
    /**
     * 插屏-跑步结束界面
     */
    val INTERSTITIAL_RUN_END: Int by lazy { INTERSTITIAL_RUN_END_ID.virtualId() }
    /**
     * 插屏-任务界面奖励下发弹窗
     */
    val INTERSTITIAL_TASK_REWARD_POPUP: Int by lazy { INTERSTITIAL_TASK_REWARD_POPUP_ID.virtualId() }
    /**
     * 插屏-非用户引导入口进入的H5界面
     */
    val INTERSTITIAL_NON_GUIDE_H5: Int by lazy { INTERSTITIAL_NON_GUIDE_H5_ID.virtualId() }

    /*------- 激励广告  -------*/
    /**
     * 任务界面多个浏览任务
     */
    val REWARD_TASK_MULTI_BROWSE: Int by lazy { REWARD_TASK_MULTI_BROWSE_ID.virtualId() }
    /**
     * 首页步数
     */
    val REWARD_HOME_STEP: Int by lazy { REWARD_HOME_STEP_ID.virtualId() }
    /**
     * 任务界面-步数提高上限
     */
    val REWARD_TASK_STEP_LIMIT_UP: Int by lazy { REWARD_TASK_STEP_LIMIT_UP_ID.virtualId() }
    /**
     * 小额兑换广告
     */
    val REWARD_SMALL_WITHDRAW: Int by lazy { REWARD_SMALL_WITHDRAW_ID.virtualId() }
    /**
     * 全局悬浮激励广告
     */
    val REWARD_GLOBAL_FLOAT: Int by lazy { REWARD_GLOBAL_FLOAT_ID.virtualId() }
    /**
     * 首页固定网页广告
     */
    val REWARD_HOME_FIXED_WEB: Int by lazy { REWARD_HOME_FIXED_WEB_ID.virtualId() }
    /**
     * 兑换页激励广告
     */
    val REWARD_WITHDRAW_PAGE: Int by lazy { REWARD_WITHDRAW_PAGE_ID.virtualId() }
    /**
     * 课程休息时间激励广告
     */
    val REWARD_COURSE_REST: Int by lazy { REWARD_COURSE_REST_ID.virtualId() }
    /**
     * 课程中途结束页激励广告
     */
    val REWARD_COURSE_MID_END: Int by lazy { REWARD_COURSE_MID_END_ID.virtualId() }
    /**
     * 课程完成结束页激励广告
     */
    val REWARD_COURSE_FINISH: Int by lazy { REWARD_COURSE_FINISH_ID.virtualId() }
    /**
     * 事件点激励广告1
     */
    val REWARD_EVENT_POINT_1: Int by lazy { REWARD_EVENT_POINT_1_ID.virtualId() }
    /**
     * 事件点激励广告2
     */
    val REWARD_EVENT_POINT_2: Int by lazy { REWARD_EVENT_POINT_2_ID.virtualId() }
    /**
     * 跑步结束页激励广告
     */
    val REWARD_RUN_END: Int by lazy { REWARD_RUN_END_ID.virtualId() }
    /**
     * 任务提高上限激励广告
     */
    val REWARD_TASK_LIMIT_UP: Int by lazy { REWARD_TASK_LIMIT_UP_ID.virtualId() }
    /**
     * 签到活动激励广告
     */
    val REWARD_ACTIVITY_SIGN_IN: Int by lazy { REWARD_ACTIVITY_SIGN_IN_ID.virtualId() }
    /**
     * 转盘活动激励广告
     */
    val REWARD_ACTIVITY_LUCKY_WHEEL: Int by lazy { REWARD_ACTIVITY_LUCKY_WHEEL_ID.virtualId() }
    /**
     * 砸蛋活动激励广告
     */
    val REWARD_ACTIVITY_SMASH_EGG: Int by lazy { REWARD_ACTIVITY_SMASH_EGG_ID.virtualId() }
    /**
     * 老虎机活动激励广告
     */
    val REWARD_ACTIVITY_SLOT_MACHINE: Int by lazy { REWARD_ACTIVITY_SLOT_MACHINE_ID.virtualId() }
    /**
     * 翻牌活动激励广告
     */
    val REWARD_ACTIVITY_FLIP_CARD: Int by lazy { REWARD_ACTIVITY_FLIP_CARD_ID.virtualId() }

    /*------- 任务界面  -------*/
    /**
     * 任务界面浏览任务
     */
    val TASK_BROWSE: Int by lazy { TASK_BROWSE_ID.virtualId() }
}

// 扩展函数：统一获取广告位 ID
fun Pair<Int, Int>.virtualId() = second
