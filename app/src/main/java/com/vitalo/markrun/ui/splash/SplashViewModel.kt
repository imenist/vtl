package com.vitalo.markrun.ui.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vitalo.markrun.data.local.prefs.AppPreferences
import com.vitalo.markrun.service.LoginManager
import com.vitalo.markrun.service.UserManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class SplashNavTarget {
    NONE, HOME, ONBOARDING
}

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val appPreferences: AppPreferences,
    private val loginManager: LoginManager,
    private val userManager: UserManager
) : ViewModel() {

    private val _progress = MutableStateFlow(0f)
    val progress = _progress.asStateFlow()

    private val _shouldNavigate = MutableStateFlow(SplashNavTarget.NONE)
    val shouldNavigate = _shouldNavigate.asStateFlow()

    private val _shouldShowAd = MutableStateFlow<Int?>(null)
    val shouldShowAd = _shouldShowAd.asStateFlow()

    private val shouldAutoNavigate: Boolean =
        appPreferences.hasLaunchedBefore && userManager.exists()

    init {
        startSplash()
    }

    private fun startSplash() {
        viewModelScope.launch {
            // Trigger auto login in background
            launch {
                loginManager.autoLogin()
            }

            val duration = if (shouldAutoNavigate) 3000L else 5000L
            val steps = 30
            val interval = duration / steps

            for (i in 1..steps) {
                delay(interval)
                _progress.value = i.toFloat() / steps
            }

            val virtualId = if (appPreferences.hasLaunchedBefore) com.vitalo.markrun.ad.Ads.SPLASH_NON_FIRST_COLD_START else com.vitalo.markrun.ad.Ads.SPLASH_FIRST_COLD_START
            _shouldShowAd.value = virtualId
        }
    }

    fun onAdCompleted() {
        _shouldShowAd.value = null
        navigateNext()
    }

    private fun navigateNext() {
        val appUiSwitchConfig = com.vitalo.markrun.common.ab.AbConfigDataRepo.getCurrentConfig(com.vitalo.markrun.common.ab.AbSidTable.APP_UI_SWITCH) as? com.vitalo.markrun.common.ab.impl.AppUiSwitchConfig
        val isInfoRegisterEnable = appUiSwitchConfig?.infoRegisterSwitch == "1"

        if (!shouldAutoNavigate && !isInfoRegisterEnable) {
            // Skip onboarding and go directly to home
            val user = com.vitalo.markrun.service.User(
                gender = com.vitalo.markrun.service.Gender.FEMALE,
                birthday = java.util.Calendar.getInstance().apply { set(1999, 0, 1) }.timeInMillis,
                height = 160,
                weight = 55
            )
            userManager.save(user)
            appPreferences.setHasLaunchedBefore(true)
            _shouldNavigate.value = SplashNavTarget.HOME
            return
        }

        if (shouldAutoNavigate) {
            _shouldNavigate.value = SplashNavTarget.HOME
        } else {
            appPreferences.setHasLaunchedBefore(true)
            _shouldNavigate.value = SplashNavTarget.ONBOARDING
        }
    }
}
