package com.vitalo.markrun.ui.common

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

object GlobalOverlayManager {
    var showSignIn by mutableStateOf(false)
        private set

    fun showSignInOverlay() {
        showSignIn = true
    }

    fun dismissSignInOverlay() {
        showSignIn = false
    }
}
