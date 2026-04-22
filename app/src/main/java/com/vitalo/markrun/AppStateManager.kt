package com.vitalo.markrun

import android.text.format.DateUtils
import com.vitalo.markrun.common.statistic.AfSdkManager
import com.vitalo.markrun.config.AppConfig
import com.vitalo.markrun.config.DevConfig
import com.vitalo.markrun.config.ProductConfig
import com.vitalo.markrun.util.MmkvUtils
import java.util.Calendar
import java.util.Date

/**
 * @Date: 2026/3/19
 **/
object AppStateManager {
    private const val CONFIG_SWITCH_ENABLE = true
    private const val KEY_ENTER_APP_COUNT = "key_enter_app_count"
    private const val KEY_HAS_APP_ENTER = "key_has_app_enter"
    private const val KEY_APP_INSTALL_TIME = "key_app_install_time"
    private const val KEY_SWITCH_CONFIG_TYPE = "key_switch_config_type"
    private const val KEY_ENTER_SHAPE_SHOOT = "key_enter_shape_shoot"
    private const val KEY_ENTER_COLOR_SHOOT = "key_enter_color_shoot"
    private const val KEY_ENTER_FACE_SHOOT = "key_enter_face_shoot"
    private const val KEY_ENTER_FIT_CHECK_SHOOT = "key_enter_fit_check_shoot"

    private var isNewUser = false
    private var pressedQuit = false

    // 服务端时间与本地时间的差值
    private var serviceTimeDiff: Long = 0

    fun updateServiceTime(serverTimeMillis: Long) {
        serviceTimeDiff = serverTimeMillis - System.currentTimeMillis()
    }

    /**
     * 获取服务器时间（如果尚未同步，则返回本地时间）
     */
    fun getServiceTime(): Long {
        val useServerTime = MmkvUtils.getBoolean("debug_use_server_time", true)
        return if (useServerTime) {
            System.currentTimeMillis() + serviceTimeDiff
        } else {
            System.currentTimeMillis()
        }
    }

    fun initialize() {
        val hasEnter = MmkvUtils.getBool(KEY_HAS_APP_ENTER)
        if (!hasEnter) {
            isNewUser = true
            MmkvUtils.putBoolean(KEY_HAS_APP_ENTER, true)
            MmkvUtils.putLong(KEY_APP_INSTALL_TIME, System.currentTimeMillis())
        }
//        installConfig()
        increaseEnterCount()
        checkAndReportRetention()
    }

    private fun checkAndReportRetention() {
        // 使用自然日差异 (0表示首日, 1表示次日...) 进行留存判断更为标准
        val retainDay = getRetainDay()
        
        // 如果想根据 24小时滚动窗口 判断，也可以使用 getAppInstallDay()，
        // 例如： val installDay = getAppInstallDay() - 1 

        val reportedKey = "reported_retention_day_$retainDay"
        if (!MmkvUtils.getBool(reportedKey)) {
            when (retainDay) {
                1 -> AfSdkManager.logEvent(AfSdkManager.EVENT_DAY1_RETENTION)
                3 -> AfSdkManager.logEvent(AfSdkManager.EVENT_DAY3_RETENTION)
                7 -> AfSdkManager.logEvent(AfSdkManager.EVENT_DAY7_RETENTION)
                14 -> AfSdkManager.logEvent(AfSdkManager.EVENT_DAY14_RETENTION)
            }
            if (retainDay in listOf(1, 3, 7, 14)) {
                MmkvUtils.putBoolean(reportedKey, true)
            }
        }
    }

    fun isNewUser(): Boolean {
        return isNewUser
    }

    /**
     * 这里由launcher activity进行打点, 而非application
     */
    fun increaseEnterCount() {
        val count = MmkvUtils.getInt(KEY_ENTER_APP_COUNT)
        MmkvUtils.putInt(KEY_ENTER_APP_COUNT, count + 1)
    }

    /**
     * 这里获取的是进入页面的次数, 而非app启动次数
     * base on [SplashActivity.onCreate]
     */
    fun getAppEnterCount(): Int {
        return MmkvUtils.getInt(KEY_ENTER_APP_COUNT)
    }

    fun isFirstEnterApp(): Boolean {
        return getAppEnterCount() <= 1
    }

    fun getAppInstallTime(): Long {
        return MmkvUtils.getLong(KEY_APP_INSTALL_TIME)
    }

    //用户留存天数 登陆天数
    fun getRetainDay(): Int {
        val installTime = getAppInstallTime()
        val nowTime = Date().time
        fun getCal(date: Long): Calendar {
            val cal = Calendar.getInstance()
            cal.time = Date(date)
            cal.set(Calendar.HOUR, 0)
            cal.set(Calendar.MINUTE, 0)
            cal.set(Calendar.SECOND, 0)
            return cal
        }

        val clearNow = getCal(nowTime).time.time
        val clearInstall = getCal(installTime).time.time
        return ((clearNow - clearInstall) / (86400000)).toInt()
    }

    fun getAppInstallDay(): Int {
        val installTime = getAppInstallTime()
        val diffTime = System.currentTimeMillis() - installTime
        var cdays = diffTime / DateUtils.DAY_IN_MILLIS + 1
        if (cdays <= 0) {
            cdays = 1
        }
        return cdays.toInt()
    }

    fun getAppInstallHour(): Int {
        val installTime = getAppInstallTime()
        val diffTime = System.currentTimeMillis() - installTime
        val hours = diffTime / DateUtils.HOUR_IN_MILLIS
        return hours.toInt()
    }

    fun isUpgradeUser(): Boolean {
        return false
    }

    fun setPressQuit(set: Boolean) {
        pressedQuit = set
        if (pressedQuit && isFirstEnterApp()) {
            // 首次通过返回按钮退出app，再快速打开并不会重新创建application，这里先+1
            increaseEnterCount()
        }
    }

    fun isPressQuit(): Boolean {
        return pressedQuit
    }


}