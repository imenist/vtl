package com.vitalo.markrun.ui.web

import android.annotation.SuppressLint
import android.webkit.JavascriptInterface
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.os.Handler
import android.os.Looper
import org.json.JSONObject
import org.json.JSONArray
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
    index: Int = -1,
    loginManager: LoginManager? = null,
    userManager: UserManager? = null,
    appPreferences: AppPreferences? = null
) {
    var isLoading by remember { mutableStateOf(true) }
    var showError by remember { mutableStateOf(false) }
    val url = remember(kind, index) { getWebGameUrl(kind, index) }

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
                            webView = this,
                            loginManager = loginManager,
                            userManager = userManager,
                            appPreferences = appPreferences,
                            onClose = { navController.popBackStack() }
                        ),
                        "MarkRun"
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

private fun getWebGameUrl(kind: String, index: Int): String {
    val language = java.util.Locale.getDefault().language
    val build = com.vitalo.markrun.BuildConfig.VERSION_CODE
    val baseUrl = when (kind) {
        "flipCard" -> "https://h5-stage.mark-run.com/game-collection/v1/index.html#/PageFlipCard"
        "slotMachine" -> "https://h5-stage.mark-run.com/game-collection/v1/index.html#/PageSlotMachine"
        "spinWheel" -> "https://h5-stage.mark-run.com/game-collection/v1/index.html#/PageWheel"
        "smashEgg" -> "https://h5-stage.mark-run.com/game-collection/v1/index.html#/PageSmashEgg"
        "signIn" -> "https://h5-stage.mark-run.com/game-collection/v1/index.html#/PageSignIn"
        "dailyRelaxation" -> "https://h5-stage.mark-run.com/game-collection/v1/index.html#/PageBlank?game=dailyRelaxation"
        "multiDailyRelaxation" -> "https://h5-stage.mark-run.com/game-collection/v1/index.html#/PageBlank?game=multiDailyRelaxation&index=$index"
        else -> "https://h5-stage.mark-run.com/game-collection/v1/index.html#/PageBlank?game=$kind"
    }
    
    // Using ? to append the first query param, but since the base URLs have a hash fragment (which might be treated differently by standard URL parsers), 
    // appending query parameters after the hash like Vue Router expects:
    val separator = if (baseUrl.contains("?")) "&" else "?"
    return "$baseUrl${separator}userLang=$language&build=$build"
}

class VitaloBridge(
    private val webView: WebView,
    private val loginManager: LoginManager?,
    private val userManager: UserManager?,
    private val appPreferences: AppPreferences?,
    private val onClose: () -> Unit
) {
    private val handler = Handler(Looper.getMainLooper())


    private fun sendToJsWhenShow() {
        val timestamp = System.currentTimeMillis().toString()
        actionToJs("GET_SERVER_TIME", timestamp)
        actionToJs("GET_USER_CACHE_COINS", "0")
        actionToJs("GET_TODAY_REWARDED_AD_WATCHED_COUNT", "{\"times\":0,\"timestamp\":0}", false)
        actionToJs("GET_APP_INSTALLED_TIME", "0")
        actionToJs("GET_CRACK_EGG_FRAGMENTS_COUNT", "")
        actionToJs("GET_TOTAL_SIGN_DAYS", "")
        actionToJs("GET_AB_CONFIG", "{}", true)
    }

    @JavascriptInterface
    fun postMessage(message: String) {
        println("[VitaloBridge] Received message from H5: $message")
        try {
            val json = JSONObject(message)
            val cmd = json.optString("cmd")
            
            // To handle both encrypted and unencrypted commands during the migration phase
            val decryptedCmd = tryDecrypt(cmd)
            val effectiveCmd = if (decryptedCmd != null) decryptedCmd else cmd
            
            when (effectiveCmd) {
                "GET_JS_VERSION" -> actionToJs("GET_JS_VERSION", "2")
                "GET_DEVICE" -> actionToJs("GET_DEVICE", "{\"platform\":\"android\",\"version\":\"1.0\"}", false)
                "GET_SERVER_TIME" -> actionToJs("GET_SERVER_TIME", System.currentTimeMillis().toString())
                "GET_AB_CONFIG" -> actionToJs("GET_AB_CONFIG", "{}", true)
                "GET_USER_CACHE_COINS" -> actionToJs("GET_USER_CACHE_COINS", "0")
                "GET_TODAY_REWARDED_AD_WATCHED_COUNT" -> actionToJs("GET_TODAY_REWARDED_AD_WATCHED_COUNT", "{\"times\":0,\"timestamp\":0}", false)
                "GET_APP_INSTALLED_TIME" -> actionToJs("GET_APP_INSTALLED_TIME", "0")
                "DISMISS_LOADING" -> println("[VitaloBridge] DISMISS_LOADING")
                "LOAD_FINISH" -> {
                    println("[VitaloBridge] LOAD_FINISH")
                    sendToJsWhenShow()
                }
                "CLOSE_WEBVIEW_CONTAINER" -> handler.post { onClose() }
                "DELIVERY_KEY_EVENT" -> println("[VitaloBridge] DELIVERY_KEY_EVENT received")
                "SHOW_NAVIGATION" -> println("[VitaloBridge] SHOW_NAVIGATION received")
                "SHOW_NAVIGATION_V2" -> println("[VitaloBridge] SHOW_NAVIGATION_V2 received")
                "NEED_INTERCEPT_KEY_EVENT" -> println("[VitaloBridge] NEED_INTERCEPT_KEY_EVENT received")
                else -> println("[VitaloBridge] Unhandled cmd: $cmd (effective: $effectiveCmd)")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun tryDecrypt(encryptedBase64: String): String? {
        try {
            val keyBytes = "Vitalo20".toByteArray(Charsets.UTF_8)
            val keySpec = javax.crypto.spec.SecretKeySpec(keyBytes, "DES")
            val cipher = javax.crypto.Cipher.getInstance("DES/ECB/PKCS5Padding")
            cipher.init(javax.crypto.Cipher.DECRYPT_MODE, keySpec)
            
            var base64 = encryptedBase64
            // Fix padding if needed
            if (base64.length % 4 != 0) {
                val paddingCount = 4 - (base64.length % 4)
                for (i in 0 until paddingCount) {
                    base64 += "="
                }
            }
            
            val decoded = android.util.Base64.decode(base64, android.util.Base64.DEFAULT)
            val decrypted = cipher.doFinal(decoded)
            return String(decrypted, Charsets.UTF_8)
        } catch (e: Exception) {
            // Check if it's already plain text (Not encrypted)
            return null
        }
    }
    
    private fun tryEncrypt(plainText: String): String {
        try {
            val keyBytes = "Vitalo20".toByteArray(Charsets.UTF_8)
            val keySpec = javax.crypto.spec.SecretKeySpec(keyBytes, "DES")
            val cipher = javax.crypto.Cipher.getInstance("DES/ECB/PKCS5Padding")
            cipher.init(javax.crypto.Cipher.ENCRYPT_MODE, keySpec)
            
            val encrypted = cipher.doFinal(plainText.toByteArray(Charsets.UTF_8))
            return android.util.Base64.encodeToString(encrypted, android.util.Base64.NO_WRAP)
        } catch (e: Exception) {
            return plainText
        }
    }

    private fun actionToJs(cmd: String, param: Any, isString: Boolean = true) {
        handler.post {
            val encryptedCmd = tryEncrypt(cmd)
            val paramStr = if (isString) "'$param'" else "$param"
            val js = "javascript:actionToJs('$encryptedCmd', $paramStr)"
            webView.evaluateJavascript(js, null)
        }
    }

    @JavascriptInterface
    fun ACCEPT_RESPONSE(message: String) {
        println("[VitaloBridge] ACCEPT_RESPONSE from H5: $message")
        postMessage(message)
    }

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
