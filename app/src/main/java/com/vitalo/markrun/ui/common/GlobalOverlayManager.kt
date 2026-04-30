package com.vitalo.markrun.ui.common

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

object GlobalOverlayManager {
    var showSignIn by mutableStateOf(false)
        private set

    var showSpinWheel by mutableStateOf(false)
        private set

    var showCoinArrived by mutableStateOf(false)
        private set

    var coinArrivedAmount by mutableStateOf(0)
        private set

    var showConversionRules by mutableStateOf(false)
        private set

    var conversionRulesExchangeRate by mutableStateOf(1000.0)
        private set

    var isLoading by mutableStateOf(false)
        private set

    fun showSignInOverlay() {
        showSignIn = true
    }

    fun dismissSignInOverlay() {
        showSignIn = false
    }

    fun showSpinWheelOverlay() {
        showSpinWheel = true
    }

    fun dismissSpinWheelOverlay() {
        showSpinWheel = false
    }

    fun showCoinArrivedOverlay(amount: Int) {
        coinArrivedAmount = amount
        showCoinArrived = true
    }

    fun dismissCoinArrivedOverlay() {
        showCoinArrived = false
    }

    fun showConversionRulesOverlay(exchangeRate: Double) {
        conversionRulesExchangeRate = exchangeRate
        showConversionRules = true
    }

    fun dismissConversionRulesOverlay() {
        showConversionRules = false
    }

    fun showLoading() {
        isLoading = true
    }

    fun dismissLoading() {
        isLoading = false
    }
}
