package com.vitalo.markrun.ui.web

import android.annotation.SuppressLint
import android.app.Activity
import android.webkit.JavascriptInterface
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.os.Handler
import android.os.Looper
import android.util.Base64
import android.util.Log
import android.widget.Toast
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
import com.vitalo.markrun.service.CoinManager
import com.vitalo.markrun.service.LoginManager
import com.vitalo.markrun.service.UserManager
import com.vitalo.markrun.util.DeviceInfoUtils
import com.vitalo.markrun.common.ab.AbConfigDataRepo
import com.vitalo.markrun.common.ab.AbSidTable
import com.vitalo.markrun.util.MmkvUtils
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

@Composable
fun WebViewScreen(
    navController: NavController,
    kind: String,
    index: Int = -1,
    loginManager: LoginManager? = null,
    userManager: UserManager? = null,
    appPreferences: AppPreferences? = null,
    coinManager: CoinManager? = null,
    deviceInfoUtils: DeviceInfoUtils? = null
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
                            coinManager = coinManager,
                            deviceInfoUtils = deviceInfoUtils,
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

                    webChromeClient = object : android.webkit.WebChromeClient() {
                        override fun onConsoleMessage(consoleMessage: android.webkit.ConsoleMessage?): Boolean {
                            Log.d("VitaloBridge", "H5 Console: ${consoleMessage?.message()} -- From line ${consoleMessage?.lineNumber()} of ${consoleMessage?.sourceId()}")
                            return super.onConsoleMessage(consoleMessage)
                        }
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
    
    val separator = if (baseUrl.contains("?")) "&" else "?"
    return "$baseUrl${separator}userLang=$language&build=$build"
}

class VitaloBridge(
    private val webView: WebView,
    private val loginManager: LoginManager?,
    private val userManager: UserManager?,
    private val appPreferences: AppPreferences?,
    private val coinManager: CoinManager?,
    private val deviceInfoUtils: DeviceInfoUtils?,
    private val onClose: () -> Unit
) {
    private val handler = Handler(Looper.getMainLooper())

    companion object {
        private const val TAG = "VitaloBridge"
        // 对标 iOS WebConstants.swift
        private const val WEB_CMD_KEY = "Vitalo2024" 
        
        // MMKV keys for ad watch tracking
        private const val KEY_TODAY_AD_WATCH_COUNT = "h5_today_rewarded_ad_watch_count"
        private const val KEY_TODAY_AD_WATCH_TIMESTAMP = "h5_today_rewarded_ad_watch_timestamp"
        // AppPreferences keys
        private const val KEY_H5_BACKUP_DATA = "h5_backup_data"
        private const val KEY_GAME_PROGRESS_PREFIX = "h5_game_progress_"
    }

    private fun sendToJsWhenShow() {
        val timestamp = System.currentTimeMillis().toString()
        actionToJs("get_server_time", timestamp)
        actionToJs("get_user_cache_coins", getCoinBalanceString())
        actionToJs("get_today_rewarded_ad_watched_count", buildAdWatchedCountJson(), false)
        actionToJs("get_app_installed_time", getAppInstalledTimeSeconds())
        // 主动下发 AB 配置（对标 iOS sendAbConfigIfAvailableOnAppear）
        val abConfig = makeAbConfigJSONString()
        actionToJs("get_ab_config", abConfig ?: "{}", false)
        // 主动推送备份数据（对标 iOS sendBackupCacheIfAvailable）
        val cached = appPreferences?.getString(KEY_H5_BACKUP_DATA) ?: "{}"
        actionToJs("back_request", cached, false)
    }

    @JavascriptInterface
    fun actionFromJs(cmd: String, paramStr: String): String {
        Log.d(TAG, "Received actionFromJs from H5: cmd=$cmd, param=$paramStr")
        try {
            val json = try {
                if (paramStr.isNotEmpty() && paramStr.startsWith("{")) JSONObject(paramStr) else JSONObject()
            } catch (e: Exception) {
                JSONObject()
            }
            
            // 对标 iOS 解密逻辑
            val decryptedCmd = tryDecrypt(cmd)
            val effectiveCmd = (if (decryptedCmd != null) decryptedCmd else cmd).uppercase()
            
            Log.d(TAG, "Effective CMD: $effectiveCmd")

            when (effectiveCmd) {
                "GET_JS_VERSION" -> actionToJs("GET_JS_VERSION", "2")

                "GET_DEVICE" -> {
                    actionToJs("GET_DEVICE", buildDeviceInfoJson(), false)
                }

                "TOAST" -> {
                    val content = json.optString("content", "")
                    val duration = json.optInt("duration", 1)
                    handler.post {
                        val toastDuration = if (duration > 2) Toast.LENGTH_LONG else Toast.LENGTH_SHORT
                        Toast.makeText(webView.context, content, toastDuration).show()
                    }
                }

                "DISMISS_LOADING" -> {
                    handler.post {
                        com.vitalo.markrun.ui.common.GlobalOverlayManager.dismissLoading()
                    }
                }

                "SHOW_LOADING" -> {
                    handler.post {
                        com.vitalo.markrun.ui.common.GlobalOverlayManager.showLoading()
                    }
                }

                "CLOSE_WEBVIEW_CONTAINER" -> handler.post { onClose() }

                "NOTIFY_APP_SHOW_AD" -> {
                    val adKey = json.optString("key", "")
                    handler.post {
                        val activity = webView.context as? Activity
                        if (activity != null) {
                            com.vitalo.markrun.ad.AdManager.showAd(activity, 0) { rewarded ->
                                val result = if (rewarded) "success" else "failed"
                                val responseJson = JSONObject().apply {
                                    put("key", adKey)
                                    put("result", result)
                                }
                                actionToJs("NOTIFY_APP_SHOW_AD", responseJson.toString(), false)
                                if (rewarded) incrementAdWatchCount()
                            }
                        } else {
                            val responseJson = JSONObject().apply {
                                put("key", adKey)
                                put("result", "failed")
                            }
                            actionToJs("NOTIFY_APP_SHOW_AD", responseJson.toString(), false)
                        }
                    }
                }

                "GET_SERVER_TIME" -> actionToJs("GET_SERVER_TIME", System.currentTimeMillis().toString())

                "GET_AB_CONFIG" -> {
                    val abConfig = makeAbConfigJSONString()
                    actionToJs("GET_AB_CONFIG", abConfig ?: "{}", false)
                }

                "NOTIFY_APP_COIN_CHANGE" -> {
                    val coinNum = json.optInt("coinNum", 0)
                    coinManager?.addCoin(coinNum)
                }

                "GET_USER_CACHE_COINS" -> {
                    actionToJs("GET_USER_CACHE_COINS", getCoinBalanceString())
                }

                "NOTIFY_APP_GAME_PROGRESS" -> {
                    val gameName = json.optString("gameName", "")
                    val times = json.optInt("times", 0)
                    val progressTimestamp = json.optLong("timestamp", 0L)
                    Log.d(TAG, "GAME_PROGRESS: $gameName times=$times")
                    
                    val progressJson = JSONObject().apply {
                        put("gameName", gameName)
                        put("times", times)
                        put("timestamp", progressTimestamp)
                    }
                    appPreferences?.setString("${KEY_GAME_PROGRESS_PREFIX}$gameName", progressJson.toString())
                    
                    // 更新 DailyTaskStatus
                    val sdf = java.text.SimpleDateFormat("yyyyMMdd", java.util.Locale.US)
                    val dateKey = sdf.format(java.util.Date(progressTimestamp))
                    val todayKey = sdf.format(java.util.Date())
                    
                    if (dateKey == todayKey) {
                        val statusKey = "DailyTaskStatus_$todayKey"
                        val status = appPreferences?.getCodable(statusKey, com.vitalo.markrun.data.remote.model.DailyTaskStatus::class.java)
                            ?: com.vitalo.markrun.data.remote.model.DailyTaskStatus.empty()
                        
                        var changed = false
                        when (gameName) {
                            "wheel" -> { status.newUserSpinCount = times; changed = true }
                            "slot" -> { status.luckySlotCount = times; changed = true }
                            "egg" -> { status.crackEggCount = times; changed = true }
                            "sign" -> { status.signInToday = (times > 0); changed = true }
                        }
                        if (changed) {
                            appPreferences?.setCodable(statusKey, status)
                            H5GameProgressNotifier.notifyProgressChanged()
                        }
                    }
                }

                "BURYING_POINT" -> {
                    val track = json.optString("track", "")
                    val properties = json.optJSONObject("properties")
                    if (track.isNotEmpty()) {
                        com.vitalo.markrun.common.statistic.StatSdkManger.upload104(
                            opCode = track,
                            statObj = properties?.toString()
                        )
                    }
                }

                "GET_TODAY_REWARDED_AD_WATCHED_COUNT" -> {
                    actionToJs("GET_TODAY_REWARDED_AD_WATCHED_COUNT", buildAdWatchedCountJson(), false)
                }

                "LOAD_FINISH" -> {
                    Log.d(TAG, "LOAD_FINISH received")
                    sendToJsWhenShow()
                }

                "BACKUP_UPLOAD" -> {
                    appPreferences?.setString(KEY_H5_BACKUP_DATA, paramStr)
                }

                "BACK_REQUEST" -> {
                    val cachedData = appPreferences?.getString(KEY_H5_BACKUP_DATA) ?: "{}"
                    actionToJs("BACK_REQUEST", cachedData, false)
                }

                "ACCEPT_RESPONSE" -> {
                    // Do nothing or handle if needed
                }

                else -> Log.d(TAG, "Unhandled cmd: $cmd (effective: $effectiveCmd)")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }

    // ─── 对标 iOS makeAbConfigJSONString ───
    private fun makeAbConfigJSONString(): String? {
        try {
            val response = AbConfigDataRepo.getCurrentResponse() ?: return null
            val gameConfigs = JSONObject()

            // 对标 iOS 定义的业务 ID
            val sids = mapOf(
                "infos_1803" to AbSidTable.NEW_USER_SPIN, // 转盘 (iOS: luckyWheel)
                "infos_1805" to AbSidTable.SIGN_REWARD,   // 签到 (iOS: dailySign)
                "infos_1807" to AbSidTable.HAMMER,        // 砸蛋 (iOS: smashEgg)
                "infos_1809" to AbSidTable.SLOT_MACHINE,  // 老虎机 (iOS: slotMachine)
                "infos_1811" to AbSidTable.FLOP_COIN,     // 翻牌 (iOS: flipCard)
                "infos_1833" to AbSidTable.AD_POLICY,     // 广告开关 (iOS: adSwitch)
                "infos_1831" to AbSidTable.AD             // 广告配置 (iOS: adConfig)
            )

            for ((key, sidContract) in sids) {
                val dataKey = "infos_${sidContract.sid}"
                val info = response.datas?.get(dataKey)
                if (info != null) {
                    val bizObj = JSONObject()
                    bizObj.put("filter_id", info.filterId ?: 0)
                    bizObj.put("abtest_id", info.abtestId ?: 0)
                    
                    val cfgsArray = JSONArray()
                    info.cfgs?.forEach { cfg ->
                        // 将 BaseAbConfig 对象转为 JSONObject
                        val cfgJson = JSONObject(MmkvUtils.gson.toJson(cfg))
                        cfgsArray.put(cfgJson)
                    }
                    bizObj.put("cfgs", cfgsArray)
                    gameConfigs.put(key, bizObj)
                }
            }
            return gameConfigs.toString()
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    private fun buildDeviceInfoJson(): String {
        val infoMap = deviceInfoUtils?.getDeviceInfoMap()
        val json = JSONObject()
        json.put("type", 1) 
        json.put("did", infoMap?.get("device_id") ?: "")
        json.put("version_number", infoMap?.get("app_version") ?: 1)
        json.put("phone_model", infoMap?.get("phone_model") ?: "")
        json.put("net_type", infoMap?.get("net_type") ?: "WiFi")
        json.put("system_version", infoMap?.get("system_version") ?: "")
        json.put("country", infoMap?.get("sim_country") ?: "")
        json.put("time_zone", infoMap?.get("zone") ?: "")
        json.put("source", -1)
        json.put("campaign", false)
        return json.toString()
    }

    private fun getCoinBalanceString(): String {
        val balance = coinManager?.coinBalance?.value ?: 0.0
        return if (balance == balance.toLong().toDouble()) balance.toLong().toString() else balance.toString()
    }

    private fun getAppInstalledTimeSeconds(): String {
        val installTimestamp = appPreferences?.getLong(AppPreferences.KEY_INSTALL_DATE) ?: 0L
        if (installTimestamp > 0) return (installTimestamp / 1000).toString()
        return try {
            val packageInfo = webView.context.packageManager.getPackageInfo(webView.context.packageName, 0)
            (packageInfo.firstInstallTime / 1000).toString()
        } catch (_: Exception) {
            "0"
        }
    }

    private fun buildAdWatchedCountJson(): String {
        val times = MmkvUtils.getInt(KEY_TODAY_AD_WATCH_COUNT, 0)
        val timestamp = MmkvUtils.getLong(KEY_TODAY_AD_WATCH_TIMESTAMP, 0L)
        val effectiveTimes = if (isToday(timestamp)) times else 0
        return JSONObject().apply {
            put("times", effectiveTimes)
            put("timestamp", if (effectiveTimes > 0) timestamp else 0)
        }.toString()
    }

    private fun incrementAdWatchCount() {
        val currentTimestamp = MmkvUtils.getLong(KEY_TODAY_AD_WATCH_TIMESTAMP, 0L)
        val currentCount = if (isToday(currentTimestamp)) MmkvUtils.getInt(KEY_TODAY_AD_WATCH_COUNT, 0) else 0
        val now = System.currentTimeMillis()
        MmkvUtils.putInt(KEY_TODAY_AD_WATCH_COUNT, currentCount + 1)
        MmkvUtils.putLong(KEY_TODAY_AD_WATCH_TIMESTAMP, now)
    }

    private fun isToday(timestamp: Long): Boolean {
        if (timestamp <= 0) return false
        val cal1 = java.util.Calendar.getInstance().apply { timeInMillis = timestamp }
        val cal2 = java.util.Calendar.getInstance()
        return cal1.get(java.util.Calendar.YEAR) == cal2.get(java.util.Calendar.YEAR) &&
               cal1.get(java.util.Calendar.DAY_OF_YEAR) == cal2.get(java.util.Calendar.DAY_OF_YEAR)
    }

    // ─── 对标 iOS DES 解密逻辑 ───
    private fun tryDecrypt(encryptedBase64: String): String? {
        try {
            // 对标 iOS：密钥 Vitalo2024，DES 只取前 8 字节
            val keyBytes = WEB_CMD_KEY.substring(0, 8).toByteArray(Charsets.UTF_8)
            val keySpec = SecretKeySpec(keyBytes, "DES")
            
            // 对标 iOS：kCCOptionPKCS7Padding | kCCOptionECBMode
            val cipher = Cipher.getInstance("DES/ECB/PKCS5Padding")
            cipher.init(Cipher.DECRYPT_MODE, keySpec)
            
            val decoded = Base64.decode(encryptedBase64, Base64.DEFAULT)
            val decrypted = cipher.doFinal(decoded)
            return String(decrypted, Charsets.UTF_8).trim()
        } catch (e: Exception) {
            return null
        }
    }
    
    private fun tryEncrypt(plainText: String): String {
        try {
            val keyBytes = WEB_CMD_KEY.substring(0, 8).toByteArray(Charsets.UTF_8)
            val keySpec = SecretKeySpec(keyBytes, "DES")
            val cipher = Cipher.getInstance("DES/ECB/PKCS5Padding")
            cipher.init(Cipher.ENCRYPT_MODE, keySpec)
            
            val encrypted = cipher.doFinal(plainText.toByteArray(Charsets.UTF_8))
            return Base64.encodeToString(encrypted, Base64.NO_WRAP)
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
        // H5 might still call this directly
    }

    @JavascriptInterface
    fun getDevice(): String {
        return buildDeviceInfoJson()
    }

    @JavascriptInterface
    fun closeWebView() {
        onClose()
    }
}
