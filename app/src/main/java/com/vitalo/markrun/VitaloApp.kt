package com.vitalo.markrun

import android.app.Activity
import android.os.Bundle
import android.app.Application
import android.content.pm.ApplicationInfo
import android.util.Log
import com.applovin.sdk.AppLovinMediationProvider
import com.applovin.sdk.AppLovinSdk
import com.applovin.sdk.AppLovinSdkInitializationConfiguration
import com.vitalo.markrun.ad.AdManager
import com.vitalo.markrun.ad.Ads
import com.vitalo.markrun.common.ab.AbConfigDataRepo
import com.vitalo.markrun.common.statistic.StatSdkManger
import com.vitalo.markrun.config.AppConfig
import com.vitalo.markrun.config.DevConfig
import com.vitalo.markrun.config.ProductConfig
import com.vitalo.markrun.util.MmkvUtils
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@HiltAndroidApp
class VitaloApp : Application() {

    private var startedActivityCount = 0
    private var isColdLaunch = true
    private var currentActivity: Activity? = null

    companion object {
        private lateinit var instance: VitaloApp
        fun getInstance(): VitaloApp = instance
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        installConfig()
        initSdk()
        initAbRepo()
        registerLifecycleCallbacks()
    }

    private fun registerLifecycleCallbacks() {
        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}

            override fun onActivityStarted(activity: Activity) {
                currentActivity = activity
                startedActivityCount++
                if (startedActivityCount == 1) {
                    onAppEnterForeground()
                }
            }

            override fun onActivityResumed(activity: Activity) {
                currentActivity = activity
            }

            override fun onActivityPaused(activity: Activity) {
                if (currentActivity === activity) {
                    currentActivity = null
                }
            }

            override fun onActivityStopped(activity: Activity) {
                startedActivityCount--
                if (startedActivityCount == 0) {
                    onAppEnterBackground()
                }
            }

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}

            override fun onActivityDestroyed(activity: Activity) {}
        })
    }

    private fun onAppEnterForeground() {
        Log.d("VitaloApp", "App enters foreground")
        if (isColdLaunch) {
            isColdLaunch = false
            // 冷启动已经在 SplashViewModel 里处理开屏广告了
        } else {
            // 热启动，展示热启动开屏广告
            currentActivity?.let { activity ->
                AdManager.showAd(activity, Ads.SPLASH_HOT_START)
            }
        }
    }

    private fun onAppEnterBackground() {
        Log.d("VitaloApp", "App enters background")
    }

    private fun initSdk() {
        MmkvUtils.initialize(this)
        StatSdkManger.initSdk(this)
        com.vitalo.markrun.common.http.HttpClient.init(this)
        // 初始化 AppLovin MAX SDK（必须在 Application 启动时调用，否则广告无法加载）
        // SDK Key 从 AndroidManifest.xml 的 applovin.sdk.key meta-data 自动读取
        val sdkKey = packageManager
            .getApplicationInfo(packageName, android.content.pm.PackageManager.GET_META_DATA)
            .metaData?.getString("applovin.sdk.key") ?: ""
        val initConfig = AppLovinSdkInitializationConfiguration.builder(sdkKey, this)
            .setMediationProvider(AppLovinMediationProvider.MAX)
            .build()
        AppLovinSdk.getInstance(this).initialize(initConfig) {
            Log.d("VitaloApp", "AppLovin SDK 初始化完成")
        }
    }

    private fun installConfig() {
        val isDebug = (applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0
        val config = if (isDebug) DevConfig() else ProductConfig()
        AppConfig.install(config)
    }

    private fun initAbRepo() {
        // 统一走 common/ab 的旧链路：先读缓存，再按 8 小时策略决定是否发请求
        AbConfigDataRepo.refreshRemoteData(false)
    }
}

