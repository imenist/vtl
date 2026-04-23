package com.vitalo.markrun.ui.debug

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.provider.Settings
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import com.vitalo.markrun.ad.AdManager
import com.vitalo.markrun.ad.Ads
import com.vitalo.markrun.util.MmkvUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun AppDebugScreen(navController: NavController) {
    val context = LocalContext.current
    val packageManager = context.packageManager
    val packageName = context.packageName
    val packageInfo = try {
        packageManager.getPackageInfo(packageName, 0)
    } catch (e: PackageManager.NameNotFoundException) {
        null
    }
    val versionName = packageInfo?.versionName ?: "Unknown"

    @Suppress("DEPRECATION")
    val versionCode = packageInfo?.versionCode?.toString() ?: "Unknown"

    val androidId = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID) ?: "Unknown"

    val isDebuggable = (context.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0
    val envName = if (isDebuggable) "Debug" else "Release"

    var gaid by remember { mutableStateOf("获取中...") }
    LaunchedEffect(Unit) {
        gaid = withContext(Dispatchers.IO) {
            try {
                AdvertisingIdClient.getAdvertisingIdInfo(context).id ?: "Unknown"
            } catch (e: Exception) {
                "Error: ${e.message}"
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // 标题栏
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(onClick = { navController.popBackStack() }) {
                Text("返回")
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text("Debug 面板", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // App信息 Panel
            var useServerTime by remember { mutableStateOf(MmkvUtils.getBoolean("debug_use_server_time", true)) }

            DebugPanel(
                title = "App信息",
                backgroundColor = Color(0xFFF2F3F4)
            ) {
                DebugTextItem("当前包名", packageName)
                DebugTextItem("当前环境", envName)
                DebugTextItem("版本号", versionCode)
                DebugTextItem("版本名称", versionName)
                DebugTextItem("AndroidId", androidId)

                val appInfo = try {
                    packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA)
                } catch (e: PackageManager.NameNotFoundException) {
                    null
                }
                val admobId = appInfo?.metaData?.getString("com.google.android.gms.ads.APPLICATION_ID") ?: "Unknown"
                DebugTextItem("AdmobAppId", admobId)
                DebugTextItem("GAID", gaid)

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "使用服务器时间", fontSize = 14.sp, color = Color.Black)
                    Switch(
                        checked = useServerTime,
                        onCheckedChange = {
                            useServerTime = it
                            MmkvUtils.putBoolean("debug_use_server_time", it)
                        }
                    )
                }
            }

            // Ad Test Panel
            DebugPanel(
                title = "广告测试",
                backgroundColor = Color.White
            ) {
                val activity = LocalContext.current as? android.app.Activity
                Column(modifier = Modifier.padding(top = 6.dp)) {
                    Text("请注意：开屏广告（Splash）暂映射为插屏展示", fontSize = 12.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(6.dp))
                    
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                        if (activity != null) {
                            Toast.makeText(activity, "开始加载 AdMob 测试插屏...", Toast.LENGTH_SHORT).show()
                            // 直接调用测试广告方法
                            AdManager.showTestAd(activity, AdManager.AdSource.ADMOB, AdManager.AdType.INTERSTITIAL_VIDEO, onAdClosed = {
                                Toast.makeText(activity, "AdMob 测试插屏关闭", Toast.LENGTH_SHORT).show()
                            })
                        }
                    }) {
                        Text(text = "请求 AdMob 测试插屏 (取代开屏测试)", fontSize = 14.sp)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                        if (activity != null) {
                            Toast.makeText(activity, "开始加载 AppLovin 测试插屏...", Toast.LENGTH_SHORT).show()
                            AdManager.showTestAd(activity, AdManager.AdSource.APPLOVIN, AdManager.AdType.INTERSTITIAL_VIDEO, onAdClosed = {
                                Toast.makeText(activity, "AppLovin 测试插屏关闭", Toast.LENGTH_SHORT).show()
                            })
                        }
                    }) {
                        Text(text = "请求 AppLovin 测试插屏 (取代开屏测试)", fontSize = 14.sp)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                        if (activity != null) {
                            Toast.makeText(activity, "开始加载 AdMob 测试激励...", Toast.LENGTH_SHORT).show()
                            AdManager.showTestAd(activity, AdManager.AdSource.ADMOB, AdManager.AdType.REWARD_VIDEO_2_0, onAdClosed = {
                                Toast.makeText(activity, "AdMob 测试激励关闭", Toast.LENGTH_SHORT).show()
                            }, onReward = {
                                Toast.makeText(activity, "获得 AdMob 测试激励奖励", Toast.LENGTH_SHORT).show()
                            })
                        }
                    }) {
                        Text(text = "请求 AdMob 测试激励", fontSize = 14.sp)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                        if (activity != null) {
                            Toast.makeText(activity, "开始加载 AppLovin 测试激励...", Toast.LENGTH_SHORT).show()
                            AdManager.showTestAd(activity, AdManager.AdSource.APPLOVIN, AdManager.AdType.REWARD_VIDEO_2_0, onAdClosed = {
                                Toast.makeText(activity, "AppLovin 测试激励关闭", Toast.LENGTH_SHORT).show()
                            }, onReward = {
                                Toast.makeText(activity, "获得 AppLovin 测试激励奖励", Toast.LENGTH_SHORT).show()
                            })
                        }
                    }) {
                        Text(text = "请求 AppLovin 测试激励", fontSize = 14.sp)
                    }
                }
            }
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun DebugPanel(
    title: String,
    backgroundColor: Color,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .padding(16.dp)
    ) {
        Text(
            text = title,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        content()
    }
}

@Composable
fun DebugTextItem(title: String, description: String) {
    Row(modifier = Modifier.padding(top = 2.dp)) {
        Text(text = "$title: ", fontSize = 14.sp, color = Color.Black)
        Text(text = description, fontSize = 14.sp, color = Color.Black)
    }
}
