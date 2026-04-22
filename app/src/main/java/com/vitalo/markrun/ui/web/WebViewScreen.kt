package com.vitalo.markrun.ui.web

import android.annotation.SuppressLint
import android.webkit.JavascriptInterface
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.vitalo.markrun.data.local.prefs.AppPreferences
import com.vitalo.markrun.service.LoginManager
import com.vitalo.markrun.service.UserManager
import com.vitalo.markrun.util.DeviceInfoUtils

@Composable
fun WebViewScreen(
    navController: NavController,
    kind: String,
    loginManager: LoginManager? = null,
    userManager: UserManager? = null,
    appPreferences: AppPreferences? = null
) {
    var isLoading by remember { mutableStateOf(true) }
    var showError by remember { mutableStateOf(false) }
    val url = remember(kind) { getWebGameUrl(kind) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        AndroidView(
            factory = { context ->
                @SuppressLint("SetJavaScriptEnabled")
                val webView = WebView(context).apply {
                    settings.javaScriptEnabled = true
                    settings.domStorageEnabled = true
                    settings.allowFileAccess = false
                    settings.setSupportZoom(false)

                    addJavascriptInterface(
                        VitaloBridge(
                            loginManager = loginManager,
                            userManager = userManager,
                            appPreferences = appPreferences,
                            onClose = { navController.popBackStack() }
                        ),
                        "VitaloBridge"
                    )

                    webViewClient = object : WebViewClient() {
                        override fun onPageFinished(view: WebView?, url: String?) {
                            isLoading = false
                        }

                        @Deprecated("Deprecated in Java")
                        override fun onReceivedError(
                            view: WebView?,
                            errorCode: Int,
                            description: String?,
                            failingUrl: String?
                        ) {
                            showError = true
                            isLoading = false
                        }

                        override fun shouldOverrideUrlLoading(
                            view: WebView?,
                            request: WebResourceRequest?
                        ): Boolean = false
                    }
                }
                webView.loadUrl(url)
                webView
            },
            modifier = Modifier.fillMaxSize()
        )

        // Close button
        Box(
            modifier = Modifier
                .statusBarsPadding()
                .padding(start = 20.dp, top = 28.dp)
                .size(40.dp)
                .clip(CircleShape)
                .background(Color.Black.copy(alpha = 0.4f))
                .clickable { navController.popBackStack() },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "✕",
                fontSize = 18.sp,
                color = Color.White.copy(alpha = 0.6f)
            )
        }

        // Loading overlay
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.85f)),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    CircularProgressIndicator(color = Color.White)
                    Text(
                        text = "Game loading.",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFF6E43)
                    )
                }
            }
        }

        // Error view
        if (showError) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.85f)),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Network Error",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                    Text(
                        text = "Tap to retry",
                        fontSize = 14.sp,
                        color = Color(0xFFCDDD44),
                        modifier = Modifier.clickable {
                            showError = false
                            isLoading = true
                        }
                    )
                }
            }
        }
    }
}

private fun getWebGameUrl(kind: String): String {
    val language = java.util.Locale.getDefault().language
    return when (kind) {
        "flipCard" -> "https://game.mark-run.com/flip-card?lang=$language"
        "slotMachine" -> "https://game.mark-run.com/slot-machine?lang=$language"
        "spinWheel" -> "https://game.mark-run.com/spin-wheel?lang=$language"
        "smashEgg" -> "https://game.mark-run.com/smash-egg?lang=$language"
        else -> "https://game.mark-run.com/$kind?lang=$language"
    }
}

class VitaloBridge(
    private val loginManager: LoginManager?,
    private val userManager: UserManager?,
    private val appPreferences: AppPreferences?,
    private val onClose: () -> Unit
) {
    @JavascriptInterface
    fun getDevice(): String {
        return "{\"platform\":\"android\",\"version\":\"1.0\"}"
    }

    @JavascriptInterface
    fun closeWebView() {
        onClose()
    }

    @JavascriptInterface
    fun getUserInfo(): String {
        val user = userManager?.currentUser
        return "{\"gender\":\"${user?.gender?.name ?: ""}\",\"weight\":${user?.weight ?: 0}}"
    }
}
