package com.vitalo.markrun.common.statistic

import android.annotation.SuppressLint
import android.app.Application
import android.util.Log
import com.cs.statistic.StatisticsManager
import com.cs.statistic.beans.OptionBean
import com.vitalo.markrun.AppStateManager
import com.vitalo.markrun.VitaloApp
import com.vitalo.markrun.config.AppConfig
import com.vitalo.markrun.common.statistic.bean.Stat19Bean
import com.vitalo.markrun.common.statistic.bean.Stat104Bean
import com.vitalo.markrun.common.statistic.bean.Stat105Bean
import com.vitalo.markrun.common.statistic.bean.Stat59Bean
import com.vitalo.markrun.util.MmkvUtils

@SuppressLint("StaticFieldLeak")
object StatSdkManger {
    const val TAG = "StatSdkManger"
    private lateinit var statisticsManager: StatisticsManager

    fun initSdk(app: Application) {
        StatisticsManager.initBasicInfo(
            AppConfig.packageName,
            AppConfig.statChannelId.toString()
        )
//        StatisticsManager.setHost(AppConfig.statBaseUrl)
        
        // 依据需求可开启
        // StatisticsManager.enableApplicationStateStatistic(
        //     app, arrayOf(...)
        // )
        
        statisticsManager = StatisticsManager.getInstance(app)
        // statisticsManager.enableLog(1)
        statisticsManager.setJobSchedulerEnable(true)
    }

    /**
     * 上传19协议统计
     */
    fun upload19(bean: Stat19Bean) {
        bean.mark = if (AppStateManager.isFirstEnterApp()) "1" else "2"
        val uploadData = bean.convertUploadString()
        StatisticsManager.getInstance(VitaloApp.getInstance())
            .uploadStaticDataForOptions(
                bean.getLogId().toInt(),
                bean.getFunId().toInt(),
                uploadData,
                null,
                OptionBean(OptionBean.OPTION_INDEX_IMMEDIATELY_ANYWAY, true)
            )
        Log.d(TAG, "上传19统计:$uploadData \n$bean")
    }

    fun upload19(
        opCode: String,
        entrance: String? = null,
        location: String? = null,
        tab: String? = null,
        statObj: String? = null,
        assignObj: String? = null,
        mark: String? = if (AppStateManager.isFirstEnterApp()) "1" else "2",
        adId: String = ""
    ) {
        upload19(Stat19Bean().apply {
            this.code = opCode
            this.entry = entrance
            this.location = location
            this.tab = tab
            this.statObj = statObj
            this.associatedObj = assignObj
            this.mark = mark
            this.adId = adId
        })
    }

    /**
     * 上传104协议统计
     */
    private fun upload104(bean: Stat104Bean) {
        bean.adId = if (AppStateManager.isFirstEnterApp()) "1" else "2"
        bean.associatedObj = AppStateManager.getRetainDay().toString()
        val uploadData = bean.convertUploadString()
        StatisticsManager.getInstance(VitaloApp.getInstance())
            .uploadStaticDataForOptions(
                bean.getLogId().toInt(),
                bean.getFunId().toInt(),
                uploadData,
                null,
                OptionBean(OptionBean.OPTION_INDEX_IMMEDIATELY_ANYWAY, true)
            )
        Log.d(TAG, "上传104统计:$uploadData \n$bean")
    }

    fun upload104(
        opCode: String,
        entrance: String? = null,
        location: String? = null,
        tab: String? = null,
        statObj: String? = null,
        assignObj: String? = AppStateManager.getRetainDay().toString(),
        mark: String? = null,
        adId: String = if (AppStateManager.isFirstEnterApp()) "1" else "2",
    ) {
        upload104(Stat104Bean().apply {
            this.code = opCode
            this.entry = entrance
            this.location = location
            this.tab = tab
            this.statObj = statObj
            this.associatedObj = assignObj
            this.mark = mark
            this.adId = adId
        })
    }

    /**
     * 上传59协议统计
     */
    fun upload59(bean: Stat59Bean) {
        val uploadData = bean.convertUploadString()
        StatisticsManager.getInstance(VitaloApp.getInstance()).upLoadStaticData(uploadData)
        Log.d(TAG, "上传59统计:$uploadData")
    }

    /**
     * 上传105协议统计
     */
    fun upload105(
        opCode: String,
        entrance: String? = null,
        location: String? = null,
        tab: String? = null,
        statObj: String? = null,
        assignObj: String? = null,
        mark: String? = null,
        adId: String = ""
    ) {
        val bean = Stat105Bean().apply {
            this.code = opCode
            this.entry = entrance
            this.location = location
            this.tab = tab
            this.statObj = statObj
            this.associatedObj = assignObj
            this.mark = mark
            this.adId = adId
        }

        val uploadData = bean.convertUploadString()
        StatisticsManager.getInstance(VitaloApp.getInstance())
            .uploadStaticDataForOptions(
                bean.getLogId().toInt(),
                bean.getFunId().toInt(),
                uploadData,
                null,
                OptionBean(OptionBean.OPTION_INDEX_IMMEDIATELY_ANYWAY, true)
            )
        Log.d(TAG, "上传105统计:$uploadData \n$bean")
    }
}
