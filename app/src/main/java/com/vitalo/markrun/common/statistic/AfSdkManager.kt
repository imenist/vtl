package com.vitalo.markrun.common.statistic

import android.app.Application
import android.os.SystemClock
import android.util.Log
import com.appsflyer.AppsFlyerConversionListener
import com.appsflyer.AppsFlyerLib
import com.appsflyer.AFInAppEventType
import com.appsflyer.AFInAppEventParameterName
import com.vitalo.markrun.VitaloApp
import com.vitalo.markrun.common.buy.BuySdkManager
import com.vitalo.markrun.common.statistic.bean.StatConst
import com.vitalo.markrun.config.AppConfig

import com.appsflyer.adrevenue.AppsFlyerAdRevenue
import com.appsflyer.adrevenue.adnetworks.generic.MediationNetwork
import java.util.Currency

object AfSdkManager {
    private const val TAG = "AfSdkManager"

    // 定义产品要求的自定义或官方事件名称常量，方便统一管理
    const val EVENT_CONTENT_VIEW = AFInAppEventType.CONTENT_VIEW // 官方: af_content_view
    const val EVENT_AD_VIEW = "af_ad_view" // 官方: af_ad_view (在一些较老版本SDK中可能没有直接常量，可以直接使用字符串)
    const val EVENT_LEVEL_ACHIEVED = AFInAppEventType.LEVEL_ACHIEVED // 官方: af_level_achieved
    const val EVENT_SPEND_CREDITS = AFInAppEventType.SPENT_CREDIT // 官方: af_spend_credits (在AFInAppEventType中一般为af_spent_credits)

    // 自定义事件
    const val EVENT_REACH_CREDITS_50 = "reach_credits_50"
    const val EVENT_REACH_CREDITS_70 = "reach_credits_70"
    const val EVENT_REACH_CREDITS_90 = "reach_credits_90"

    // 广告打点
    const val EVENT_REWARDED_AD_DOUBLE = "rewarded_ad_double"
    
    // 首日激励广告打点
    const val EVENT_DAY0_AD_RW_3 = "day0_ad_rw_3"
    const val EVENT_DAY0_AD_RW_5 = "day0_ad_rw_5"
    const val EVENT_DAY0_AD_RW_7 = "day0_ad_rw_7"

    // 首日看剧集数打点
    const val EVENT_DAY0_WATCH_EPS_1 = "day0_watch_eps1"
    const val EVENT_DAY0_WATCH_EPS_5 = "day0_watch_eps5"
    const val EVENT_DAY0_WATCH_EPS_10 = "day0_watch_eps10"

    // 首日看剧时长打点
    const val EVENT_DAY0_DURATION_30 = "day0_duration_30"
    const val EVENT_DAY0_DURATION_60 = "day0_duration_60"
    const val EVENT_DAY0_DURATION_120 = "day0_duration_120"

    // 留存打点
    const val EVENT_DAY1_RETENTION = "day1_retention"
    const val EVENT_DAY3_RETENTION = "day3_retention"
    const val EVENT_DAY7_RETENTION = "day7_retention"
    const val EVENT_DAY14_RETENTION = "day14_retention"

    // 广告收益打点
    const val EVENT_REELC_AD_REVENUE = "reelc_ad_revenue"

    fun initSdk(application: Application) {
        // AppsFlyer 归因回调监听
        val conversionListener = object : AppsFlyerConversionListener {
            override fun onConversionDataSuccess(data: MutableMap<String, Any>?) {
                Log.w(TAG, "onConversionDataSuccess: $data")
                val costTimeFloat = (SystemClock.elapsedRealtime() - BuySdkManager.buyLaunchTime) / 1000f
                val formattedCostTime = String.format(java.util.Locale.US, "%.1f", costTimeFloat)
                StatSdkManger.upload104(
                    opCode = StatConst.t000_start_play_af,
                    statObj = formattedCostTime,
                    entrance = "1",
                    tab = BuySdkManager.getUserFrom().toString()
                )
                // 若有处理归因数据的需求可以在此处理
            }

            override fun onConversionDataFail(error: String?) {
                Log.w(TAG, "onConversionDataFail: $error")
                val costTimeFloat = (SystemClock.elapsedRealtime() - BuySdkManager.buyLaunchTime) / 1000f
                val formattedCostTime = String.format(java.util.Locale.US, "%.1f", costTimeFloat)
                StatSdkManger.upload104(
                    opCode = StatConst.t000_start_play_af,
                    statObj = formattedCostTime,
                    entrance = "2_$error",
                    tab = BuySdkManager.getUserFrom().toString()
                )
            }

            override fun onAppOpenAttribution(data: MutableMap<String, String>?) {
                Log.w(TAG, "onAppOpenAttribution: $data")
            }

            override fun onAttributionFailure(error: String?) {
                Log.w(TAG, "onAttributionFailure: $error")
            }
        }
        AppsFlyerLib.getInstance().setDebugLog(true)
        // 买量 SDK（BuyChannelApi）内 AppsFlyerProxy 会先 init(devKey, 其自有 listener)。AF 只处理第一次 init，
        // 若仅把 listener 传给第二次 init，回调不会进这里；事件仍走已初始化的单例故后台有数。
        // 统一用 init(..., null) + registerConversionListener，无论买量先后都能挂上本处监听，且避免与 init 重复注册同一 listener。
//        AppsFlyerLib.getInstance().init(AppConfig.afDevKey, null, application)
        AppsFlyerLib.getInstance().registerConversionListener(application, conversionListener)

        // 初始化 AppsFlyer AdRevenue SDK
        val afRevenueBuilder = AppsFlyerAdRevenue.Builder(application)
        AppsFlyerAdRevenue.initialize(afRevenueBuilder.build())

        // 开启 AppsFlyer
        AppsFlyerLib.getInstance().start(application)

    }

    /**
     * AppsFlyer 打点 (Event Tracking)
     * 支持官方事件（如 com.appsflyer.AFInAppEventType.CONTENT_VIEW）或自定义事件
     * 
     * @param eventName 事件名称（AFInAppEventType中的常量 或 自定义字符串）
     * @param eventValues 事件参数映射（AFInAppEventParameterName中的常量 或 自定义参数）
     */
    fun logEvent(eventName: String, eventValues: Map<String, Any>? = null) {
        val context = VitaloApp.getInstance()
        AppsFlyerLib.getInstance().logEvent(context, eventName, eventValues)
        Log.d(TAG, "AF LogEvent -> Name: $eventName, Values: $eventValues")
    }

    /**
     * 记录广告收益 (针对 AppLovin MAX)
     */
    fun logAdRevenue(networkName: String, revenue: Double, currencyCode: String = "USD") {
        try {
            val customParams = HashMap<String, String>()
            customParams[AFInAppEventParameterName.CURRENCY] = currencyCode
            customParams[AFInAppEventParameterName.REVENUE] = revenue.toString()
            customParams["network_name"] = networkName
            
            // 使用 AppsFlyerAdRevenue SDK 上报
            AppsFlyerAdRevenue.logAdRevenue(
                networkName,
                MediationNetwork.customMediation,
                Currency.getInstance(currencyCode),
                revenue,
                customParams
            )
            
            // 根据需求额外上报自定义事件 "reelc_ad_revenue"
            logEvent(EVENT_REELC_AD_REVENUE, customParams)
            Log.d(TAG, "AF LogAdRevenue -> network: $networkName, revenue: $revenue $currencyCode")
        } catch (e: Exception) {
            Log.e(TAG, "AF LogAdRevenue error: ${e.message}")
        }
    }
}
