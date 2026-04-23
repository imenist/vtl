package com.vitalo.markrun

import android.app.Application
import android.content.pm.ApplicationInfo
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
        installSdk()
        installConfig()
        initAbManager()
    }

    private fun installSdk(){
        MmkvUtils.initialize(this)
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

