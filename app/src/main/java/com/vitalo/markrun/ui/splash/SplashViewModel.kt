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

            navigateNext()
        }
    }

    private fun navigateNext() {
        if (shouldAutoNavigate) {
            _shouldNavigate.value = SplashNavTarget.HOME
        } else {
            appPreferences.setHasLaunchedBefore(true)
            _shouldNavigate.value = SplashNavTarget.ONBOARDING
        }
    }
}
