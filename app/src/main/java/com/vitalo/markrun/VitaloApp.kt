package com.vitalo.markrun

import android.app.Application
import android.content.pm.ApplicationInfo
import android.util.Log
import com.applovin.sdk.AppLovinMediationProvider
import com.applovin.sdk.AppLovinSdk
import com.applovin.sdk.AppLovinSdkInitializationConfiguration
import com.vitalo.markrun.ab.AbManager
import com.vitalo.markrun.config.AppConfig
import com.vitalo.markrun.config.DevConfig
import com.vitalo.markrun.config.ProductConfig
import com.vitalo.markrun.util.MmkvUtils
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class VitaloApp : Application() {

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface VitaloAppEntryPoint {
        fun abManager(): AbManager
    }

    companion object {
        private lateinit var instance: VitaloApp
        fun getInstance(): VitaloApp = instance
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        installSdk()
        installConfig()
        initAbManager()
    }

    private fun installSdk() {
        MmkvUtils.initialize(this)
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

    private fun initAbManager() {
        val entryPoint = EntryPointAccessors.fromApplication(
            this, VitaloAppEntryPoint::class.java
        )
        entryPoint.abManager().init()
    }
}

