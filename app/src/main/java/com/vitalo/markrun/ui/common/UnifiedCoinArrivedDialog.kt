package com.vitalo.markrun.ui.common

import androidx.compose.runtime.Composable

@Composable
fun UnifiedCoinArrivedDialog(
    coinNum: Int,
    desc: String = "CaloCoins Have Arrived",
    getButtonText: String = "Got it!",
    showBalance: Boolean = true,
    showAdButton: Boolean = false,
    showFingerGuide: Boolean = false,
    onClose: (Boolean) -> Unit = {},
    onAdButtonTap: () -> Unit = {}
) {
    val coinExchangeRate = 1000.0 // Hardcoded for now, similar to ExchangeScreen

    if (coinExchangeRate > 0) {
        CoinArrivedWithProgressDialog(
            coinNum = coinNum,
            desc = desc,
            getButtonText = getButtonText,
            showBalance = showBalance,
            showAdButton = showAdButton,
            showFingerGuide = showFingerGuide,
            onClose = onClose,
            onAdButtonTap = onAdButtonTap
        )
    } else {
        CoinArrivedDialog(
            coinNum = coinNum,
            desc = desc,
            getButtonText = getButtonText,
            showBalance = showBalance,
            showAdButton = showAdButton,
            showFingerGuide = showFingerGuide,
            onClose = onClose,
            onAdButtonTap = onAdButtonTap
        )
    }
}
