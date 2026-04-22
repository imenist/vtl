# Vitalo (MarkRun) iOS → Android 迁移实施文档

> 本文档为 iOS 项目 Vitalo（商店名 MarkRun）迁移至 Android (Jetpack Compose) 的完整实施手册。
> 每个 Phase 可在独立的 Cursor 窗口中通过 `@MIGRATION_PLAN.md` 引用执行。

---

## 目录

1. [项目总览与技术栈](#1-项目总览与技术栈)
2. [工程配置与依赖](#2-工程配置与依赖)
3. [包结构](#3-包结构)
4. [Phase 1：基础框架](#4-phase-1基础框架)
5. [Phase 2：主页与课程模块](#5-phase-2主页与课程模块)
6. [Phase 3：跑步模块](#6-phase-3跑步模块)
7. [Phase 4：任务/签到/兑换/收集](#7-phase-4任务签到兑换收集)
8. [Phase 5：个人中心与其他](#8-phase-5个人中心与其他)
9. [附录 A：技术映射速查表](#9-附录-a技术映射速查表)
10. [附录 B：网络层各域配置差异表](#10-附录-b网络层各域配置差异表)
11. [附录 C：资源迁移](#11-附录-c资源迁移)
12. [附录 D：风险与注意事项](#12-附录-d风险与注意事项)
13. [附录 E：iOS 源文件路径速查](#13-附录-e-ios-源文件路径速查)

---

## 1. 项目总览与技术栈

### 1.1 iOS 项目现状

| 维度 | iOS 现状 |
|------|----------|
| 工程名 | Vitalo |
| 商店名 | MarkRun |
| 语言 | Swift (纯 SwiftUI + 少量 UIKit 桥接) |
| 架构 | MVVM (`Views/` + `ViewModels/` + `Models/` + `Services/`) |
| 数据库 | GRDB (SQLite) |
| 网络 | Alamofire + 自定义 DES 加密 + HMAC-SHA256 签名 |
| 入口 | `VitaloApp.swift` → `ContentView.swift` (NavigationStack) |

### 1.2 App 功能概要

- **运动模块**：课程浏览/跟练视频/户外跑步(地图轨迹)
- **任务与签到**：每日任务/签到/步数转换
- **兑换与收集**：代币体系/碎片收集/提现
- **H5 小游戏**：翻牌/老虎机/转盘/砸蛋（WebView 承载）
- **个人中心**：跑步统计/体重图表/最近活动/设置

### 1.3 Android 技术栈

| 层 | 技术 |
|----|------|
| UI | Jetpack Compose |
| 导航 | Navigation Compose |
| DI | Hilt |
| 网络 | Retrofit + OkHttp |
| 数据库 | Room |
| 图片 | Coil |
| 动画 | Lottie Compose |
| 视频 | Media3 ExoPlayer |
| 地图 | Google Maps Compose |
| KV 存储 | DataStore / SharedPreferences |
| 安全存储 | EncryptedSharedPreferences |
| 加密 | javax.crypto (DES) + javax.crypto.Mac (HMAC-SHA256) |

### 1.4 第一版跳过的模块

- 广告相关（AppLovin、AdMob、开屏/插屏/激励）
- 统计/归因（ThinkingSDK、Firebase Analytics、AppsFlyer）
- WebView 预热/复用池
- 好评引导
- 调试面板

---

## 2. 工程配置与依赖

### 2.1 包名

```
com.vitalo.markrun
```

需要修改：
- `app/build.gradle.kts` 中的 `namespace` 和 `applicationId`
- `app/src/main/AndroidManifest.xml`
- 源码目录结构 `app/src/main/java/com/vitalo/markrun/`

### 2.2 libs.versions.toml

```toml
[versions]
agp = "8.11.2"
kotlin = "2.0.21"
ksp = "2.0.21-1.0.28"
coreKtx = "1.17.0"
junit = "4.13.2"
junitVersion = "1.3.0"
espressoCore = "3.7.0"
appcompat = "1.7.1"
material = "1.13.0"

# Compose
composeBom = "2024.12.01"
activityCompose = "1.9.3"
lifecycleRuntimeCompose = "2.8.7"
navigationCompose = "2.8.5"

# Hilt
hilt = "2.51.1"
hiltNavigationCompose = "1.2.0"

# Room
room = "2.6.1"

# Network
retrofit = "2.11.0"
okhttp = "4.12.0"
gson = "2.11.0"

# Image
coil = "2.7.0"

# Lottie
lottieCompose = "6.6.2"

# ExoPlayer (Media3)
media3 = "1.5.1"

# Google Maps
mapsCompose = "6.4.0"
playServicesLocation = "21.3.0"
playServicesMaps = "19.0.0"

# DataStore
datastore = "1.1.1"

# Security
securityCrypto = "1.1.0-alpha06"

# Charts
vico = "2.0.1"

[libraries]
androidx-core-ktx = { group = "androidx.core", name = "core-ktx", version.ref = "coreKtx" }
junit = { group = "junit", name = "junit", version.ref = "junit" }
androidx-junit = { group = "androidx.test.ext", name = "junit", version.ref = "junitVersion" }
androidx-espresso-core = { group = "androidx.test.espresso", name = "espresso-core", version.ref = "espressoCore" }
androidx-appcompat = { group = "androidx.appcompat", name = "appcompat", version.ref = "appcompat" }
material = { group = "com.google.android.material", name = "material", version.ref = "material" }

# Compose
compose-bom = { group = "androidx.compose", name = "compose-bom", version.ref = "composeBom" }
compose-ui = { group = "androidx.compose.ui", name = "ui" }
compose-ui-graphics = { group = "androidx.compose.ui", name = "ui-graphics" }
compose-ui-tooling = { group = "androidx.compose.ui", name = "ui-tooling" }
compose-ui-tooling-preview = { group = "androidx.compose.ui", name = "ui-tooling-preview" }
compose-material3 = { group = "androidx.compose.material3", name = "material3" }
compose-foundation = { group = "androidx.compose.foundation", name = "foundation" }
activity-compose = { group = "androidx.activity", name = "activity-compose", version.ref = "activityCompose" }
lifecycle-runtime-compose = { group = "androidx.lifecycle", name = "lifecycle-runtime-compose", version.ref = "lifecycleRuntimeCompose" }
lifecycle-viewmodel-compose = { group = "androidx.lifecycle", name = "lifecycle-viewmodel-compose", version.ref = "lifecycleRuntimeCompose" }
navigation-compose = { group = "androidx.navigation", name = "navigation-compose", version.ref = "navigationCompose" }

# Hilt
hilt-android = { group = "com.google.dagger", name = "hilt-android", version.ref = "hilt" }
hilt-compiler = { group = "com.google.dagger", name = "hilt-compiler", version.ref = "hilt" }
hilt-navigation-compose = { group = "androidx.hilt", name = "hilt-navigation-compose", version.ref = "hiltNavigationCompose" }

# Room
room-runtime = { group = "androidx.room", name = "room-runtime", version.ref = "room" }
room-compiler = { group = "androidx.room", name = "room-compiler", version.ref = "room" }
room-ktx = { group = "androidx.room", name = "room-ktx", version.ref = "room" }

# Network
retrofit = { group = "com.squareup.retrofit2", name = "retrofit", version.ref = "retrofit" }
retrofit-converter-gson = { group = "com.squareup.retrofit2", name = "converter-gson", version.ref = "retrofit" }
retrofit-converter-scalars = { group = "com.squareup.retrofit2", name = "converter-scalars", version.ref = "retrofit" }
okhttp = { group = "com.squareup.okhttp3", name = "okhttp", version.ref = "okhttp" }
okhttp-logging = { group = "com.squareup.okhttp3", name = "logging-interceptor", version.ref = "okhttp" }
gson = { group = "com.google.code.gson", name = "gson", version.ref = "gson" }

# Image
coil-compose = { group = "io.coil-kt", name = "coil-compose", version.ref = "coil" }

# Lottie
lottie-compose = { group = "com.airbnb.android", name = "lottie-compose", version.ref = "lottieCompose" }

# Media3
media3-exoplayer = { group = "androidx.media3", name = "media3-exoplayer", version.ref = "media3" }
media3-ui = { group = "androidx.media3", name = "media3-ui", version.ref = "media3" }

# Google Maps
maps-compose = { group = "com.google.maps.android", name = "maps-compose", version.ref = "mapsCompose" }
play-services-location = { group = "com.google.android.gms", name = "play-services-location", version.ref = "playServicesLocation" }
play-services-maps = { group = "com.google.android.gms", name = "play-services-maps", version.ref = "playServicesMaps" }

# DataStore
datastore-preferences = { group = "androidx.datastore", name = "datastore-preferences", version.ref = "datastore" }

# Security
security-crypto = { group = "androidx.security", name = "security-crypto", version.ref = "securityCrypto" }

# Charts
vico-compose = { group = "com.patrykandpatrick.vico", name = "compose-m3", version.ref = "vico" }

[plugins]
android-application = { id = "com.android.application", version.ref = "agp" }
kotlin-android = { id = "org.jetbrains.kotlin.android", version.ref = "kotlin" }
kotlin-compose = { id = "org.jetbrains.kotlin.plugin.compose", version.ref = "kotlin" }
ksp = { id = "com.google.devtools.ksp", version.ref = "ksp" }
hilt = { id = "com.google.dagger.hilt.android", version.ref = "hilt" }
```

### 2.3 根 build.gradle.kts

```kotlin
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.hilt) apply false
}
```

### 2.4 app/build.gradle.kts

```kotlin
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
}

android {
    namespace = "com.vitalo.markrun"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.vitalo.markrun"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    // Core
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)

    // Compose
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.graphics)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material3)
    implementation(libs.compose.foundation)
    implementation(libs.activity.compose)
    implementation(libs.lifecycle.runtime.compose)
    implementation(libs.lifecycle.viewmodel.compose)
    implementation(libs.navigation.compose)
    debugImplementation(libs.compose.ui.tooling)

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.hilt.navigation.compose)

    // Room
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)

    // Network
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.retrofit.converter.scalars)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging)
    implementation(libs.gson)

    // Image
    implementation(libs.coil.compose)

    // Lottie
    implementation(libs.lottie.compose)

    // Media3 (ExoPlayer)
    implementation(libs.media3.exoplayer)
    implementation(libs.media3.ui)

    // Google Maps
    implementation(libs.maps.compose)
    implementation(libs.play.services.location)
    implementation(libs.play.services.maps)

    // DataStore
    implementation(libs.datastore.preferences)

    // Security
    implementation(libs.security.crypto)

    // Charts
    implementation(libs.vico.compose)

    // Test
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
```

### 2.5 AndroidManifest.xml 权限

```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_BACKGROUND_LOCATION" />
<uses-permission android:name="android.permission.ACTIVITY_RECOGNITION" />
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
<uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
<uses-permission android:name="android.permission.FOREGROUND_SERVICE_LOCATION" />
```

---

## 3. 包结构

```
com.vitalo.markrun/
├── VitaloApp.kt                          # @HiltAndroidApp Application
├── MainActivity.kt                       # @AndroidEntryPoint Single Activity
├── navigation/
│   ├── Screen.kt                         # sealed class Screen
│   └── NavGraph.kt                       # NavHost composable
├── data/
│   ├── local/
│   │   ├── db/
│   │   │   ├── VitaloDatabase.kt         # @Database
│   │   │   ├── dao/
│   │   │   │   ├── RunningRecordDao.kt
│   │   │   │   ├── RunningPointDao.kt
│   │   │   │   └── WeightRecordDao.kt
│   │   │   └── entity/
│   │   │       ├── RunningRecord.kt
│   │   │       ├── RunningPoint.kt
│   │   │       └── WeightRecord.kt
│   │   ├── prefs/
│   │   │   └── AppPreferences.kt         # SharedPreferences 封装
│   │   └── secure/
│   │       └── SecureStorage.kt          # EncryptedSharedPreferences
│   ├── remote/
│   │   ├── api/
│   │   │   ├── TrainingApi.kt            # Retrofit interface
│   │   │   ├── AccountApi.kt
│   │   │   ├── CoinApi.kt
│   │   │   ├── GameApi.kt
│   │   │   └── ABTestApi.kt
│   │   ├── interceptor/
│   │   │   ├── TrainingInterceptor.kt    # Query + Signature + Encrypt + Headers
│   │   │   ├── AccountCenterInterceptor.kt
│   │   │   ├── CoinInterceptor.kt        # 复用 AccountCenter + X-Auth-Token
│   │   │   └── ABTestInterceptor.kt
│   │   ├── crypto/
│   │   │   ├── DESEncryptor.kt           # DES-ECB-PKCS5
│   │   │   └── HMACSigner.kt            # HMAC-SHA256 → URL-safe Base64
│   │   └── model/
│   │       ├── CommonResponse.kt
│   │       ├── Training.kt
│   │       ├── Lesson.kt
│   │       ├── AutoLoginResult.kt
│   │       ├── Token.kt
│   │       └── ... (其他 DTO)
│   └── repository/
│       ├── TrainingRepository.kt
│       ├── AccountRepository.kt
│       ├── CoinRepository.kt
│       ├── GameRepository.kt
│       └── ABTestRepository.kt
├── di/
│   ├── AppModule.kt                      # Room, Prefs, SecureStorage
│   └── NetworkModule.kt                  # 各域 OkHttpClient + Retrofit
├── ui/
│   ├── theme/
│   │   ├── Color.kt
│   │   ├── Type.kt
│   │   └── Theme.kt
│   ├── splash/
│   │   ├── SplashScreen.kt
│   │   └── SplashViewModel.kt
│   ├── onboarding/
│   │   ├── BeginnerGuideScreen.kt
│   │   ├── GenderSelectionScreen.kt
│   │   ├── BirthdaySelectionScreen.kt
│   │   ├── HeightSelectionScreen.kt
│   │   ├── WeightSelectionScreen.kt
│   │   └── OnboardingViewModel.kt
│   ├── home/
│   │   ├── HomeScreen.kt
│   │   ├── CustomTabBar.kt
│   │   └── HomeViewModel.kt
│   ├── lesson/
│   │   ├── LessonScreen.kt
│   │   ├── LessonViewModel.kt
│   │   ├── LessonCardView.kt
│   │   └── ...
│   ├── lessondetail/
│   ├── followalong/
│   ├── running/
│   ├── task/
│   ├── exchange/
│   ├── collection/
│   ├── signin/
│   ├── mine/
│   ├── settings/
│   ├── recentactivities/
│   ├── stepcounter/
│   ├── web/
│   ├── spinwheel/
│   ├── permission/
│   └── common/
│       ├── CommonLottieView.kt
│       ├── CommonLoadingView.kt
│       ├── CommonErrorView.kt
│       ├── CommonEmptyView.kt
│       ├── CoinArrivedDialog.kt
│       └── ...
├── service/
│   ├── LoginManager.kt
│   ├── LocationService.kt
│   ├── StepCounterService.kt
│   └── UserManager.kt
└── util/
    ├── Constants.kt                      # Base URLs, API Keys, Secrets
    ├── DeviceInfoUtils.kt
    └── Extensions.kt
```

---

## 4. Phase 1：基础框架

> 预计工期：1-2 周
> 目标：项目骨架搭建完毕，能编译运行，完成启动页和新手引导流程

### 4.1 任务清单

| # | 任务 | 对应 iOS 源文件 | Android 产出文件 |
|---|------|----------------|-----------------|
| 1 | 改包名 + 配置依赖 | `Podfile` | `build.gradle.kts`, `libs.versions.toml`, `settings.gradle.kts` |
| 2 | Application + MainActivity | `VitaloApp.swift` | `VitaloApp.kt`, `MainActivity.kt` |
| 3 | 主题与颜色 | 各 View 中的 `Color(hex:)` | `Color.kt`, `Type.kt`, `Theme.kt` |
| 4 | 常量定义 | `Constants/*.swift` | `Constants.kt` |
| 5 | DES 加密工具 | `DESEncryptor.swift` | `DESEncryptor.kt` |
| 6 | HMAC 签名工具 | `HMACSigner.swift` | `HMACSigner.kt` |
| 7 | 网络拦截器 (Training) | `Strategies/Training/*.swift` | `TrainingInterceptor.kt` |
| 8 | 网络拦截器 (AccountCenter) | `Strategies/AccountCenter/*.swift` | `AccountCenterInterceptor.kt` |
| 9 | 网络拦截器 (Coin/Game) | `Strategies/Coin/*.swift` | `CoinInterceptor.kt` |
| 10 | 网络拦截器 (ABTest) | `Strategies/ABTest/*.swift` | `ABTestInterceptor.kt` |
| 11 | CommonResponse 模型 | `CommonResponse.swift` | `CommonResponse.kt` |
| 12 | API DTO 模型 | `Models/API/` 下各文件 | `data/remote/model/*.kt` |
| 13 | Retrofit API 接口 | `Repository/*.swift` | `data/remote/api/*.kt` |
| 14 | Hilt DI 模块 | — | `NetworkModule.kt`, `AppModule.kt` |
| 15 | Repository 层 | `Repository/*.swift` | `data/repository/*.kt` |
| 16 | Room 数据库 | `DatabaseManager.swift` | `VitaloDatabase.kt` + Entity + DAO |
| 17 | AppPreferences | `AppUserDefaults.swift` | `AppPreferences.kt` |
| 18 | SecureStorage | `CoinKeychainManager.swift` 等 | `SecureStorage.kt` |
| 19 | LoginManager | `LoginManager.swift` | `LoginManager.kt` |
| 20 | UserManager | `UserManager.swift` | `UserManager.kt` |
| 21 | 路由定义 | `Route.swift` | `Screen.kt` |
| 22 | NavGraph 骨架 | `ContentView.swift` | `NavGraph.kt` |
| 23 | SplashScreen | `SplashView.swift`, `SplashViewModel.swift` | `SplashScreen.kt`, `SplashViewModel.kt` |
| 24 | CommonLottieView | `CommonLottieView.swift` | `CommonLottieView.kt` |
| 25 | Onboarding 流程 | `BeginneGuide/*.swift` | `ui/onboarding/*.kt` |
| 26 | Lottie 资源复制 | `Resources/Lottie/` (P0) | `assets/lottie/` |
| 27 | 图片资源提取 (P0) | `Assets.xcassets/` | `res/drawable-*` |

### 4.2 关键代码模板

#### 4.2.1 DESEncryptor.kt

iOS 使用 CommonCrypto 的 DES-ECB-PKCS7，Android 用 `javax.crypto.Cipher` (DES/ECB/PKCS5Padding)。
PKCS5 和 PKCS7 对 DES 8 字节块等效。

```kotlin
import android.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

object DESEncryptor {
    fun encrypt(data: ByteArray, key: ByteArray): ByteArray {
        val cipher = Cipher.getInstance("DES/ECB/PKCS5Padding")
        cipher.init(Cipher.ENCRYPT_MODE, SecretKeySpec(key, "DES"))
        return cipher.doFinal(data)
    }

    fun decrypt(data: ByteArray, key: ByteArray): ByteArray {
        val cipher = Cipher.getInstance("DES/ECB/PKCS5Padding")
        cipher.init(Cipher.DECRYPT_MODE, SecretKeySpec(key, "DES"))
        return cipher.doFinal(data)
    }

    /**
     * 处理密钥：如果是 Base64 编码则先解码，否则 UTF-8 转字节。
     * 最终截取/填充为 8 字节。
     * 对应 iOS DESEncryptor.decodeKeyIfNeed(key:isKeyEncoded:)
     */
    fun decodeKeyIfNeed(key: String, isKeyEncoded: Boolean): ByteArray {
        var processedKey = key
        // 补齐到 4 的倍数（用 '0' 填充）
        val remainder = processedKey.length % 4
        if (remainder != 0) {
            processedKey = processedKey.padEnd(processedKey.length + (4 - remainder), '0')
        }

        val keyBytes = if (isKeyEncoded) {
            // URL-safe Base64 → 标准 Base64
            val standardBase64 = processedKey
                .replace('-', '+')
                .replace('_', '/')
            // 补齐 Base64 '=' padding
            val padded = standardBase64.let {
                val miss = it.length % 4
                if (miss > 0) it + "=".repeat(4 - miss) else it
            }
            Base64.decode(padded, Base64.DEFAULT)
        } else {
            processedKey.toByteArray(Charsets.UTF_8)
        }

        // 截取或填充为 8 字节
        return when {
            keyBytes.size > 8 -> keyBytes.copyOf(8)
            keyBytes.size < 8 -> keyBytes.copyOf(8) // copyOf 自动以 0 填充
            else -> keyBytes
        }
    }

    /** 加密后转 URL-safe Base64 字符串（无 padding） */
    fun encryptToUrlSafeBase64(data: ByteArray, key: ByteArray): String {
        val encrypted = encrypt(data, key)
        return Base64.encodeToString(encrypted, Base64.NO_WRAP)
            .replace('+', '-')
            .replace('/', '_')
            .trimEnd('=')
    }

    /** 从 URL-safe Base64 字符串解密 */
    fun decryptFromUrlSafeBase64(encoded: String, key: ByteArray): ByteArray {
        var standardBase64 = encoded.replace('-', '+').replace('_', '/')
        val miss = standardBase64.length % 4
        if (miss > 0) standardBase64 += "=".repeat(4 - miss)
        val decoded = Base64.decode(standardBase64, Base64.DEFAULT)
        return decrypt(decoded, key)
    }
}
```

#### 4.2.2 HMACSigner.kt

```kotlin
import android.util.Base64
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

object HMACSigner {
    /**
     * HMAC-SHA256 签名，输出 URL-safe Base64（无 padding）。
     * 对应 iOS HMACSigner.hmacSHA256(message:key:)
     */
    fun hmacSHA256(message: String, key: String): String {
        val mac = Mac.getInstance("HmacSHA256")
        mac.init(SecretKeySpec(key.toByteArray(Charsets.UTF_8), "HmacSHA256"))
        val result = mac.doFinal(message.toByteArray(Charsets.UTF_8))
        return Base64.encodeToString(result, Base64.NO_WRAP)
            .replace('+', '-')
            .replace('/', '_')
            .trimEnd('=')
    }
}
```

#### 4.2.3 网络拦截器模板（以 Training 域为例）

iOS 的策略模式（QueryHandler + PayloadHandler + SignatureProvider + HeadersHandler + ResponseDecryptor）
在 Android 侧合并为一个 OkHttp Interceptor（请求拦截 + 响应拦截）。

```kotlin
class TrainingInterceptor : Interceptor {
    private val apiKey = Constants.TRAINING_API_KEY
    private val apiSecret = Constants.TRAINING_API_SECRET
    private val desKey = DESEncryptor.decodeKeyIfNeed(Constants.TRAINING_DES_KEY, isKeyEncoded = true)

    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val method = original.method.uppercase()
        val path = original.url.encodedPath

        // --- 请求阶段 ---

        // 1. Query 注入 api_key + timestamp
        val urlBuilder = original.url.newBuilder()
            .addQueryParameter("api_key", apiKey)
            .addQueryParameter("timestamp", System.currentTimeMillis().toString())

        // 2. 读取原始 payload（用于签名）
        val rawPayloadJson = original.body?.let { body ->
            val buffer = okio.Buffer()
            body.writeTo(buffer)
            buffer.readUtf8()
        } ?: ""

        // 3. 加密 payload → URL-safe Base64
        val encryptedBody = if (rawPayloadJson.isNotEmpty()) {
            val sortedJson = sortJsonKeys(rawPayloadJson)
            DESEncryptor.encryptToUrlSafeBase64(
                sortedJson.toByteArray(Charsets.UTF_8), desKey
            )
        } else ""

        // 4. 签名：METHOD\npath\nqueryString\npayloadJson
        val builtUrl = urlBuilder.build()
        val queryString = builtUrl.queryParameterNames.joinToString("&") { name ->
            "$name=${builtUrl.queryParameter(name)}"
        }
        val signString = "$method\n$path\n$queryString\n$rawPayloadJson"
        val signature = HMACSigner.hmacSHA256(signString, apiSecret)

        // 5. 构建新请求
        val newRequest = original.newBuilder()
            .url(builtUrl)
            .header("X-Signature", signature)
            .header("Content-Type", "application/json;charset=UTF-8")
            .header("X-Crypto", "des")
            .method(method,
                if (method != "GET") encryptedBody.toRequestBody("application/json".toMediaType())
                else null
            )
            .build()

        // --- 响应阶段 ---
        val response = chain.proceed(newRequest)
        val isEncrypted = response.header("X-Encrypted")?.lowercase() == "true"

        return if (isEncrypted) {
            val responseBody = response.body?.string() ?: ""
            val decryptedBytes = DESEncryptor.decryptFromUrlSafeBase64(responseBody, desKey)
            response.newBuilder()
                .body(String(decryptedBytes, Charsets.UTF_8).toResponseBody(response.body?.contentType()))
                .build()
        } else {
            response
        }
    }
}
```

**各域差异**：见 [附录 B](#10-附录-b网络层各域配置差异表)。

#### 4.2.4 CommonResponse.kt

```kotlin
import com.google.gson.annotations.SerializedName

data class CommonResponse<T>(
    @SerializedName("error_code") val errorCode: Int,
    @SerializedName("error_message") val errorMessage: String?,
    val data: T?
) {
    val isSuccess: Boolean get() = errorCode == 0
}
```

#### 4.2.5 Room 数据库

```kotlin
@Entity(tableName = "runningRecord")
data class RunningRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: String,
    val startTime: Long,
    val endTime: Long,
    val duration: Int,          // 秒
    val distance: Double,       // 米
    val speed: Double,          // 米/秒
    val calories: Double,
    val startLatitude: Double,
    val startLongitude: Double,
    val endLatitude: Double,
    val endLongitude: Double,
    val lowIntensityTime: Int,
    val moderateIntensityTime: Int,
    val highIntensityTime: Int,
    val imagePath: String?,
    val fragmentsCount: Int = 0,
    val coins: Int = 0,
    val adCoins: Int? = null
) {
    val pace: Double get() = if (distance > 0) (duration / 60.0) / (distance / 1000.0) else 0.0
    val paceString: String get() {
        val minutes = pace.toInt()
        val seconds = ((pace - minutes) * 60).toInt()
        return "${minutes}'${String.format("%02d", seconds)}\""
    }
}

@Entity(
    tableName = "runningPoint",
    foreignKeys = [ForeignKey(
        entity = RunningRecord::class,
        parentColumns = ["id"],
        childColumns = ["recordId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["recordId", "timestamp"])]
)
data class RunningPoint(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val recordId: Long,
    val latitude: Double,
    val longitude: Double,
    val timestamp: Long,
    val speed: Double
)

@Entity(tableName = "weightRecord", indices = [Index(value = ["date"])])
data class WeightRecord(
    @PrimaryKey val id: String,  // UUID
    val date: Long,
    val weight: Int              // kg
)

@Database(
    entities = [RunningRecord::class, RunningPoint::class, WeightRecord::class],
    version = 1,
    exportSchema = false
)
abstract class VitaloDatabase : RoomDatabase() {
    abstract fun runningRecordDao(): RunningRecordDao
    abstract fun runningPointDao(): RunningPointDao
    abstract fun weightRecordDao(): WeightRecordDao
}
```

#### 4.2.6 LoginManager.kt

```kotlin
@Singleton
class LoginManager @Inject constructor(
    private val accountRepository: AccountRepository,
    private val appPreferences: AppPreferences,
    private val secureStorage: SecureStorage
) {
    private var currentResult: AutoLoginResult? = null
    private var savedTime: Long? = null

    init { loadFromPrefs() }

    val isTokenExpired: Boolean get() {
        val token = currentResult?.token ?: return true
        val saved = savedTime ?: return true
        return (System.currentTimeMillis() / 1000 - saved) > token.expiredIn
    }

    suspend fun autoLogin(forceRefresh: Boolean = false): String? {
        if (!forceRefresh && currentResult != null && !isTokenExpired) {
            return currentResult?.token?.accessToken
        }
        // 尝试 refresh
        if (isTokenExpired && currentResult?.token?.refreshToken != null) {
            try {
                val resp = accountRepository.refreshToken(currentResult!!.token.refreshToken!!)
                if (resp.isSuccess && resp.data != null) {
                    val newResult = currentResult!!.copy(token = resp.data)
                    saveResult(newResult)
                    return resp.data.accessToken
                }
                if (resp.errorCode == 3008) {
                    return executeAutoLogin()
                }
            } catch (_: Exception) {}
        }
        return executeAutoLogin()
    }

    private suspend fun executeAutoLogin(): String? {
        return try {
            val resp = accountRepository.autoLogin()
            if (resp.isSuccess && resp.data != null) {
                saveResult(resp.data)
                resp.data.token.accessToken
            } else null
        } catch (_: Exception) { null }
    }

    private fun saveResult(result: AutoLoginResult) { /* save to prefs */ }
    private fun loadFromPrefs() { /* load from prefs */ }
}
```

#### 4.2.7 Navigation Screen 定义

```kotlin
sealed class Screen(val route: String) {
    data object Splash : Screen("splash")
    data object BeginnerGuide : Screen("beginner_guide")
    data object Gender : Screen("gender")
    data object Birthday : Screen("birthday")
    data object Weight : Screen("weight")
    data object Height : Screen("height")
    data object Home : Screen("home")
    data object LessonDetail : Screen("lesson_detail/{trainingCode}/{partIndex}/{fromToday}") {
        fun createRoute(trainingCode: String, partIndex: Int, fromToday: Boolean) =
            "lesson_detail/$trainingCode/$partIndex/$fromToday"
    }
    data object ActionDetail : Screen("action_detail/{actionCode}")
    data object RunTracker : Screen("run_tracker")
    data object FollowAlong : Screen("follow_along/{trainingCode}/{lessonCode}")
    data object LocationPermission : Screen("location_permission")
    data object RunningResult : Screen("running_result/{recordId}/{fragmentsCount}")
    data object WorkoutResult : Screen("workout_result")
    data object Mine : Screen("mine")
    data object Settings : Screen("settings")
    data object RecentActivities : Screen("recent_activities")
    data object RunningDetail : Screen("running_detail/{recordId}")
    data object WebGame : Screen("web_game/{kind}")
    data object WithdrawRecord : Screen("withdraw_record")
}
```

#### 4.2.8 SplashScreen (简化版)

```kotlin
@Composable
fun SplashScreen(
    navController: NavController,
    viewModel: SplashViewModel = hiltViewModel()
) {
    val progress by viewModel.progress.collectAsState()
    val shouldNavigate by viewModel.shouldNavigate.collectAsState()

    LaunchedEffect(shouldNavigate) {
        when (shouldNavigate) {
            SplashNavTarget.HOME -> navController.navigate(Screen.Home.route) {
                popUpTo(Screen.Splash.route) { inclusive = true }
            }
            SplashNavTarget.ONBOARDING -> navController.navigate(Screen.BeginnerGuide.route) {
                popUpTo(Screen.Splash.route) { inclusive = true }
            }
            else -> {}
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // 背景 Lottie
        CommonLottieView(animationName = "SplashBackground", loop = false)
        // 前景 Lottie
        CommonLottieView(animationName = "SplashForeground", loop = false)
        // 进度条
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier.align(Alignment.BottomCenter).padding(48.dp).fillMaxWidth()
        )
    }
}
```

#### 4.2.9 CommonLottieView

```kotlin
@Composable
fun CommonLottieView(
    animationName: String,
    loop: Boolean = true,
    modifier: Modifier = Modifier
) {
    val composition by rememberLottieComposition(
        LottieCompositionSpec.Asset("lottie/$animationName/data.json")
    )
    val progress by animateLottieCompositionAsState(
        composition,
        iterations = if (loop) LottieConstants.IterateForever else 1
    )
    LottieAnimation(
        composition = composition,
        progress = { progress },
        modifier = modifier
    )
}
```

#### 4.2.10 Constants.kt（密钥常量）

```kotlin
object Constants {
    // Training
    const val TRAINING_BASE_URL = "https://flash-fit-core-api-stage.3g.net.cn"
    const val TRAINING_API_KEY = "ZsxOwSEQvhquD3jSAHkF8z0q"
    const val TRAINING_API_SECRET = "wFID5SOkNZtWOAsWSoyCozlIIQWiX6B6"
    const val TRAINING_DES_KEY = "qEXOutWnp54"

    // AccountCenter
    const val ACCOUNT_BASE_URL = "https://searn-state.3g.net.cn"
    const val ACCOUNT_API_KEY = "ZSrzTeZAA5BCIggxaGroBKNd"
    const val ACCOUNT_API_SECRET = "mLyx3Q4O41i69ssXsiqYMiiV1JgW796b"
    const val ACCOUNT_DES_KEY = "O5iZQwpg"

    // Coin
    const val COIN_BASE_URL = "https://scoins-state.3g.net.cn"
    // Coin 复用 AccountCenter 的密钥

    // Game
    const val GAME_BASE_URL = "https://sfquiz-state.3g.net.cn"
    // Game 复用 AccountCenter/Coin 密钥

    // ABTest
    const val ABTEST_BASE_URL = "https://control.mark-run.com"
    const val ABTEST_CID = "1125"
    const val ABTEST_PRODUCT_KEY = "SC2GXN9BBG33RJP8Q7JFAA50"
    const val ABTEST_ACCESS_KEY = "MFK71I80F1R89RCLJINUQN1BCYUQ"
    const val ABTEST_SECRET_KEY = "H7SDYH9X"
    const val ABTEST_ENTRANCE = 999
}
```

### 4.3 验收标准

- [ ] 项目能成功编译
- [ ] 包名为 `com.vitalo.markrun`
- [ ] 启动后显示 SplashScreen（Lottie 背景 + 前景 + 进度条）
- [ ] 首次启动进入 BeginnerGuide → Gender → Birthday → Height → Weight → Home
- [ ] 非首次启动直接进入 Home
- [ ] DES 加密/解密 单元测试通过（用 iOS 已知明文密文对验证）
- [ ] HMAC 签名 单元测试通过
- [ ] LoginManager 能完成自动登录并保存 Token
- [ ] Room 数据库创建成功，包含 3 张表

---

## 5. Phase 2：主页与课程模块

> 预计工期：2-3 周
> 目标：主页 Tab 容器 + 课程列表 + 课程详情 + 跟练视频

### 5.1 任务清单

| # | 任务 | 对应 iOS 文件 |
|---|------|-------------|
| 1 | HomeScreen + TabView 容器 | `HomeView.swift` |
| 2 | CustomTabBar（底部自定义导航栏，中间凸起跑步按钮） | `CustomTabBar.swift` |
| 3 | LessonScreen（课程列表 + Subject 分区 + 下拉刷新） | `LessonView.swift`, `LessonContentView.swift` |
| 4 | TodayTrainingCardView | `TodayTrainingCardView.swift` |
| 5 | LessonCardView | `LessonCardView.swift` |
| 6 | LessonDetailScreen（课程详情 + 动作列表） | `LessonDetailView.swift` |
| 7 | ActionInstructionScreen | `ActionInstructionView.swift` |
| 8 | FollowAlongScreen（跟练视频 - ExoPlayer） | `FollowAlongView.swift`, `FollowAlongViewModel.swift` |
| 9 | RestScreen（休息倒计时） | `RestPageView.swift` |
| 10 | WorkoutResultScreen | `WorkoutResultView.swift` |
| 11 | TrainingApi Retrofit 接口 | `TrainingApiRepository.swift` |
| 12 | LessonViewModel | `LessonViewModel.swift` |
| 13 | LessonDetailViewModel | `LessonDetailViewModel.swift` |
| 14 | FollowAlongViewModel | `FollowAlongViewModel.swift` |
| 15 | 公共组件 | `CommonNetworkErrorView.swift`, `CommonEmptyDataView.swift`, `CommonLoadingView.swift` |
| 16 | EarnRulesDialog | `EarnRulesDialog.swift` |
| 17 | DailyUnlockPackDialog | `DailyUnlockPackDialog.swift` |
| 18 | TopBar 金币组件 (首页/任务页复用) | `CoinBalanceView.swift` |
| 19 | TopBar 右上角互动组件 | `AdGamePlayButton`, `RulesButton`, `MineButton` |

### 5.2 关键实现要点

**HomeScreen Tab 结构**：

| Tab 位置 | 内容 | 图标 |
|----------|------|------|
| 0 | LessonScreen（课程） | `ic_home_tab_training` |
| 1 | TaskScreen（任务） | `ic_home_tab_task` |
| 2 | 跑步入口 | `ic_home_tab_running`（中间凸起圆形按钮） |
| 3 | ExchangeScreen 或 CollectionScreen | `ic_home_tab_exchange`（AB 测试控制顺序） |
| 4 | CollectionScreen 或 ExchangeScreen | `ic_home_tab_collection` |

- Tab 2（跑步）点击不切 Tab，而是 `navigate` 到 RunTracker 或 LocationPermission
- TabBar 样式：暗色背景 `#0D120E`，顶部圆角 20dp，边缘绿黄渐变光晕
- Compose 实现：`Scaffold` + 自定义 `BottomBar` composable，**不用** `BottomNavigation`

**顶部导航栏 (TopBar) 控件**：
- **左侧 Redeem/金币组件**：复用 `CoinBalanceView`，展示金币总余额。在首页 `LessonScreen` 中需传入 `showExchange = true`，以渲染右侧的 "Redeem" 动画交互按钮。
- **右上角互动栏控件 (首页 `LessonScreen` 中)**：
  - `AdGamePlayButton`：游戏/广告控件入口（主页右上角的 play 等控件）。
  - `RulesButton`：疑难解答/赚币规则弹窗按钮（对应图标 `ic_earn_rules`）。
  - `MineButton`：个人中心入口按钮（对应图标 `ic_personal`）。

**ExoPlayer 视频跟练**：
- 对应 iOS `AVPlayerViewController`
- 使用 Media3 `ExoPlayer` + `PlayerView`
- FollowAlongViewModel 管理：当前动作索引、计时器、休息切换

### 5.3 TrainingApi 接口

```kotlin
interface TrainingApi {
    @POST("/api/v1/subject")
    suspend fun fetchSubjectList(@Body payload: Map<String, Any>): CommonResponse<SubjectListPage>

    @POST("/api/v1/lesson")
    suspend fun fetchLessonDetail(@Body payload: Map<String, Any>): CommonResponse<Lesson>

    @POST("/api/v1/action")
    suspend fun fetchActionDetail(@Body payload: Map<String, Any>): CommonResponse<Action>

    @POST("/api/v1/recommend")
    suspend fun fetchRecommendPlan(@Body payload: Map<String, Any>): CommonResponse<Plan>

    @POST("/api/v1/plan")
    suspend fun fetchPlanDetail(@Body payload: Map<String, Any>): CommonResponse<Plan>

    @POST("/api/v1/album")
    suspend fun fetchAlbum(@Body payload: Map<String, Any>): CommonResponse<Album>
}
```

> 注意：由于请求体需要先被 Interceptor 加密为 DES 字符串，实际 Retrofit 接口可能需要使用 `@Body RequestBody` 而不是 `Map`。具体实现需要根据 Interceptor 的设计决定。推荐方案：Interceptor 拦截前 body 仍为 JSON，Interceptor 内部读取并替换为加密后的 body。

### 5.4 验收标准

- [ ] 首页显示 5 个 Tab（含中间凸起跑步按钮）
- [ ] 课程列表从 API 加载并展示
- [ ] 课程详情页展示动作列表
- [ ] 跟练视频能正常播放（ExoPlayer）
- [ ] 跟练结果页正确展示
- [ ] 下拉刷新正常工作

---

## 6. Phase 3：跑步模块

> 预计工期：2-3 周
> 目标：完整的跑步功能，包含地图轨迹、实时数据、结果页

### 6.1 任务清单

| # | 任务 | 对应 iOS 文件 |
|---|------|-------------|
| 1 | RunningScreen（跑步主页面） | `RunningView.swift` |
| 2 | Google Maps 集成 + 轨迹绘制 | `RunningMapView.swift`, `Coordinator.swift` |
| 3 | RunningTitleBar | `RunningTitleBar.swift` |
| 4 | LocationService（Fused Location） | iOS `LocationService` |
| 5 | StartCountDownOverlay (3-2-1) | `StartCountDownOverlay.swift` |
| 6 | 暂停/恢复 Overlay | `RunningView.swift` 内部 |
| 7 | RunningSummarySheet | `RunningSummarySheet.swift` |
| 8 | RunningResultScreen | `RunningResultView.swift` |
| 9 | RunningDetailScreen | `RunningDetailView.swift` |
| 10 | RunningViewModel | `RunningViewModel.swift` |
| 11 | RunningResultViewModel | `RunningResultViewModel.swift` |
| 12 | 跑步记录 Room DAO 方法 | — |
| 13 | LocationPermissionScreen | `LocationPermissionView.swift` |
| 14 | 定位权限弹窗 | `LocationPermissionDeniedDialog.swift`, `CommonPermissionDeniedDialog.swift` |
| 15 | RewardPopupOverlay | `RewardPopupOverlay.swift` (P1) |

### 6.2 关键实现要点

**Google Maps Compose**：
```kotlin
@Composable
fun RunningMapView(
    locations: List<LatLng>,
    cameraPositionState: CameraPositionState
) {
    GoogleMap(
        cameraPositionState = cameraPositionState,
        properties = MapProperties(isMyLocationEnabled = true)
    ) {
        if (locations.size >= 2) {
            Polyline(
                points = locations,
                color = GradientGreen,
                width = 8f
            )
        }
    }
}
```

**LocationService**：使用 `FusedLocationProviderClient` 的 `requestLocationUpdates`，回调更新轨迹点列表。

**前台 Service**：跑步时需要 `ForegroundService` 保证后台定位不被杀。在 `AndroidManifest.xml` 中声明：
```xml
<service
    android:name=".service.RunningForegroundService"
    android:foregroundServiceType="location"
    android:exported="false" />
```

### 6.3 验收标准

- [ ] 定位权限请求流程正常
- [ ] 3-2-1 倒计时后开始跑步
- [ ] 地图实时显示轨迹
- [ ] 配速、距离、时间实时更新
- [ ] 暂停/恢复/结束正常
- [ ] 跑步结果页展示数据正确
- [ ] 跑步记录保存到 Room 数据库
- [ ] 跑步详情页能查看历史记录的地图轨迹

---

## 7. Phase 4：任务/签到/兑换/收集

> 预计工期：2-3 周
> 目标：完成任务系统、签到弹窗、兑换提现、卡片收集、计步

### 7.1 任务清单

| # | 任务 | 对应 iOS 文件 |
|---|------|-------------|
| 1 | TaskScreen | `TaskView.swift`, `TaskContentView.swift` |
| 2 | DailyTaskView | `DailyTaskView.swift` |
| 3 | TaskViewModel | `TaskViewModel.swift` |
| 4 | SignInScreen（弹窗 Overlay + 日历） | `SignInView.swift` 系列 |
| 5 | SignInViewModel | `SignInViewModel.swift` |
| 6 | ExchangeScreen（兑换/钱包主页） | `ExchangeView.swift` |
| 7 | WithdrawScreen（提现） | `WithdrawlView.swift` |
| 8 | WithdrawFillInfoDialog | `WithdrawFillInfoDialog.swift` |
| 9 | WithdrawCodeDialog | `WithdrawCodeDialog.swift` |
| 10 | WithdrawQueuingDialog | `WithdrawQueuingDialog.swift` |
| 11 | WithdrawRecordScreen | `WithdrawRecordView.swift` |
| 12 | ExchangeFragmentDialog | `ExchangeFragmentDialog.swift` |
| 13 | ExchangeViewModel | `ExchangeViewModel.swift` |
| 14 | CollectionScreen（卡片收集/图鉴） | `CollectionView.swift` |
| 15 | CardDetailOverlay | `CardDetailOverlay.swift` |
| 16 | CollectionViewModel | `CollectionViewModel.swift` |
| 17 | StepCounterScreen | `StepCounterView.swift` |
| 18 | StepCounterViewModel | `StepCounterViewModel.swift` |
| 19 | StepCounterService（SensorManager） | iOS CMPedometer |
| 20 | CoinApi + GameApi Retrofit 接口 | `CoinApiRepository.swift`, `GameApiRepository.swift` |
| 21 | CoinBalanceViewModel | `CoinBalanceViewModel.swift` |
| 22 | UnifiedCoinArrivedDialog | `UnifiedCoinArrivedDialog.swift` |
| 23 | DailyRelaxationView (P1) | `DailyRelaxationView.swift` |

### 7.2 关键实现要点

**任务页顶部导航 (TopBar)**：
- 任务页 (`TaskScreen`) 作为 Overlay 复用 `CoinBalanceView`，展示金币总余额，但不展示 Redeem（传入 `showExchange = false`）。

**StepCounterService**：使用 `SensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)`，需要 `ACTIVITY_RECOGNITION` 权限 (API 29+)。

**CoinApi 接口**：
```kotlin
interface CoinApi {
    @POST("/ISO1880200")
    suspend fun getCoinTypes(@Body payload: Map<String, Any>): CommonResponse<CoinTypeList>

    @POST("/ISO1880211")
    suspend fun getCoinInfos(@Body payload: Map<String, Any>): CommonResponse<CoinInfoList>

    @POST("/ISO1880202")
    suspend fun orderOptCoin(@Body payload: Map<String, Any>): CommonResponse<String>

    @POST("/ISO1880203")
    suspend fun optCoin(@Body payload: Map<String, Any>): CommonResponse<EmptyData>
}
```

**GameApi 接口**：
```kotlin
interface GameApi {
    @POST("/ISO1880520")
    suspend fun getWithdrawalConfig(@Body payload: Map<String, Any>): CommonResponse<WithdrawalConfig>

    @POST("/ISO1880617")
    suspend fun withdraw(@Body payload: Map<String, Any>): CommonResponse<WithdrawalResult>

    @POST("/ISO1880615")
    suspend fun getWithdrawalRecords(@Body payload: Map<String, Any>): CommonResponse<WithdrawalRecords>

    @POST("/ISO1880619")
    suspend fun queryWithdrawalStatus(@Body payload: Map<String, Any>): CommonResponse<MerchantInfos>
}
```

### 7.3 验收标准

- [ ] 任务列表正常显示与交互
- [ ] 签到弹窗日历展示正确
- [ ] 兑换/钱包页面展示余额与选项
- [ ] 提现流程完整（填写信息 → 提交 → 兑换码/排队弹窗）
- [ ] 提现记录页正常
- [ ] 卡片收集网格展示 + 详情 Overlay
- [ ] 计步功能正常（传感器读数 + 转换逻辑）

---

## 8. Phase 5：个人中心与其他

> 预计工期：1-2 周
> 目标：个人中心、设置、最近活动、体重图表、WebView、转盘

### 8.1 任务清单

| # | 任务 | 对应 iOS 文件 |
|---|------|-------------|
| 1 | MineScreen（个人中心 + 跑步统计 + 最佳记录） | `MineView.swift`, `BestRecordView.swift` |
| 2 | MineViewModel | `MineViewModel.swift` |
| 3 | SettingsScreen | `SettingsView.swift` |
| 4 | RecentActivitiesScreen | `RecentActivitiesView.swift` |
| 5 | WeightChartScreen（Vico 图表） | `WeightChartView.swift` 系列 |
| 6 | AddWeightDialog | `AddWeightDialog.swift` |
| 7 | WeightChartViewModel | `WeightChartViewModel.swift` |
| 8 | CustomCalendarView (P1) | `CustomCalendarView.swift` 系列 |
| 9 | WebViewScreen + JS Bridge | `WebView.swift`, `BaseWebView.swift`, `WebViewMessageHandler.swift` |
| 10 | SpinWheelScreen (P1) | `SpinWheelView.swift` |
| 11 | SpinWheelViewModel (P1) | `SpinWheelViewModel.swift` |
| 12 | FlipCardOverlayView (P1) | `FlipCardOverlayView.swift` |
| 13 | FeedbackDialog (P1) | `FeedbackDialog.swift` |
| 14 | GuideOverlayView (P1) | `GuideOverlayView.swift` |

### 8.2 关键实现要点

**Vico 体重图表**：
```kotlin
@Composable
fun WeightChart(records: List<WeightRecord>) {
    // 使用 Vico CartesianChartHost
    // X 轴：日期
    // Y 轴：体重 (kg)
}
```

**WebView + JS Bridge**：
```kotlin
@Composable
fun WebGameScreen(url: String) {
    AndroidView(factory = { ctx ->
        WebView(ctx).apply {
            settings.javaScriptEnabled = true
            addJavascriptInterface(WebBridge(), "VitaloBridge")
            loadUrl(url)
        }
    })
}
```

### 8.3 验收标准

- [ ] 个人中心展示跑步统计（总距离/时长/卡路里/最佳记录）
- [ ] 设置页各选项正常
- [ ] 最近活动列表 + 点击查看详情
- [ ] 体重图表正常绘制
- [ ] WebView 能加载 H5 页面 + JS Bridge 通信正常
- [ ] 转盘动画与交互正常 (P1)

---

## 9. 附录 A：技术映射速查表

| iOS 模式 | Android 实现 | 说明 |
|----------|-------------|------|
| `@StateObject VM` | `hiltViewModel()` | 屏幕级 ViewModel |
| `@EnvironmentObject` | Hilt `@Singleton` / `CompositionLocal` | 全局共享状态 |
| `@State` | `remember { mutableStateOf() }` | 局部 UI 状态 |
| `@Binding` | 回调 lambda `(T) -> Unit` | 父子双向绑定 |
| `@Published` | `MutableStateFlow` / `MutableState` | 可观察属性 |
| `ObservableObject` | `ViewModel` | 状态持有者 |
| `NavigationProxy.path.append(route)` | `navController.navigate(route)` | 页面跳转 |
| `@Environment(\.dismiss)` | `navController.popBackStack()` | 页面返回 |
| `onReceive(publisher)` | `LaunchedEffect { flow.collect {} }` | 响应式监听 |
| `.onAppear` | `LaunchedEffect(Unit) {}` | 首次出现 |
| `.onChange(of: value)` | `LaunchedEffect(value) {}` | 值变化监听 |
| `fullScreenCover` | `Dialog` / 全屏 composable | 全屏弹窗 |
| `.overlay` / `ZStack` | `Box` 叠层 | 叠加视图 |
| `.sheet` | `ModalBottomSheet` | 底部弹出 |
| `ScrollView + LazyVStack` | `LazyColumn` | 懒加载列表 |
| `GeometryReader` | `BoxWithConstraints` / `Modifier.onSizeChanged` | 布局测量 |
| `AsyncImage (Kingfisher)` | `AsyncImage (Coil)` | 异步图片 |
| `Timer.publish` | `delay()` in coroutine / `ticker` | 定时器 |
| `NotificationCenter` | `SharedFlow` / `EventBus` / `BroadcastReceiver` | 事件广播 |
| `UserDefaults` | `SharedPreferences` / `DataStore` | KV 存储 |
| `Keychain` | `EncryptedSharedPreferences` | 安全存储 |
| `DES (CommonCrypto)` | `DES (javax.crypto)` | 加密 |
| `HMAC (CryptoSwift)` | `HMAC (javax.crypto.Mac)` | 签名 |

---

## 10. 附录 B：网络层各域配置差异表

### 域配置总表

| 域 | Base URL | API Key | Secret | DES Key | isKeyEncoded |
|---|---------|---------|--------|---------|-------------|
| Training | `flash-fit-core-api-stage.3g.net.cn` | `ZsxOwSEQvhquD3jSAHkF8z0q` | `wFID5SOkNZtWOAsWSoyCozlIIQWiX6B6` | `qEXOutWnp54` | true |
| AccountCenter | `searn-state.3g.net.cn` | `ZSrzTeZAA5BCIggxaGroBKNd` | `mLyx3Q4O41i69ssXsiqYMiiV1JgW796b` | `O5iZQwpg` | true |
| Coin | `scoins-state.3g.net.cn` | 复用 AccountCenter | 复用 AccountCenter | 复用 AccountCenter | true |
| Game | `sfquiz-state.3g.net.cn` | 复用 AccountCenter | 复用 AccountCenter | 复用 AccountCenter | true |
| ABTest | `control.mark-run.com` | — (无) | `MFK71I80F1R89RCLJINUQN1BCYUQ` (签名用) | `H7SDYH9X` | false |

### 各域 Interceptor 行为差异

| 步骤 | Training | AccountCenter | Coin/Game | ABTest |
|------|----------|---------------|-----------|--------|
| **Query 注入** | `api_key` + `timestamp` | `api_key` + `timestamp` | `api_key` + `timestamp` | 无通用参数（调用方自带） |
| **Payload 加密** | JSON→DES→URL-safe Base64 字符串作为 body | 同 Training | 同 Training | 无 body (GET) |
| **签名拼接** | `METHOD\npath\nqueryString\npayloadJson` | 同 Training | 同 Training | 同 Training 但 payload 固定为空字符串 |
| **签名密钥** | `trainingApiSecret` | `accountCenterApiSecret` | `accountCenterApiSecret` | `abTestAccessKey` |
| **签名 Header** | `X-Signature` | `X-Signature` | `X-Signature` | `X-Signature` |
| **Content-Type** | `application/json;charset=UTF-8` | `application/json;charset=UTF-8` | `application/json;charset=UTF-8` | 不同（无标准 Content-Type） |
| **其他 Header** | `X-Crypto: des` | `X-Crypto: des` | `X-Crypto: des` + **`X-Auth-Token`** (登录后) | `Server-Encrypt: true` + `timestamp` + `isABTestCenter` |
| **响应解密判断** | 检查 `X-Encrypted: true` | 检查 `X-Crypto: des` | 检查 `X-Crypto: des` | 始终解密（无条件） |
| **响应解密方式** | body 为 URL-safe Base64 → 解码 → DES 解密 | body 为**原始 DES 密文**（非 Base64）→ 直接解密 | 同 AccountCenter | body 为 URL-safe Base64 → 解码 → DES 解密 |

### AccountApi 接口

```kotlin
interface AccountApi {
    @POST("/ISO1880102")
    suspend fun autoLogin(@Body payload: Map<String, Any>): CommonResponse<AutoLoginResult>

    @POST("/ISO1880106")
    suspend fun refreshToken(@Body payload: Map<String, Any>): CommonResponse<TokenWrapper>
}
```

### ABTestApi 接口

```kotlin
interface ABTestApi {
    @GET("/ISO1850001")
    suspend fun getABTest(
        @Query("gzip") gzip: String = "0",
        @Query("pkgname") pkgName: String,
        @Query("sid") sid: String,
        @Query("cid") cid: String = Constants.ABTEST_CID,
        @Query("cversion") cversion: String,
        @Query("local") local: String,
        @Query("entrance") entrance: Int = Constants.ABTEST_ENTRANCE,
        @Query("cdays") cdays: String,
        @Query("aid") aid: String,
        @Query("user_from") userFrom: String,
        @Query("prodkey") prodkey: String = Constants.ABTEST_PRODUCT_KEY
    ): ABTestResult
}
```

---

## 11. 附录 C：资源迁移

### 11.1 图片资源（321 个 imageset）

**来源**：`/Users/he/Downloads/vitalo/Vitalo/Assets.xcassets/`

**迁移步骤**：
1. 遍历每个 `.imageset` 文件夹
2. 读取 `Contents.json` 找到 `@2x` / `@3x` 的 PNG 文件
3. `@2x` → `drawable-xhdpi/`, `@3x` → `drawable-xxhdpi/`
4. 文件名转换：驼峰/中划线 → `snake_case`，全小写

### 11.2 Lottie 动画（27 个）

**来源**：`/Users/he/Downloads/vitalo/Vitalo/Resources/Lottie/`

**迁移步骤**：
1. 复制每个动画文件夹到 `app/src/main/assets/lottie/`
2. 保持结构：`lottie/{动画名}/data.json` + `images/`（如有）

**P0 动画清单**（首版必须）：
- `SplashBackground` — 启动页背景
- `SplashForeground` — 启动页前景
- `CoinArrivedDialog` — 金币到账弹窗
- `AddCoinEffect` — 金币增加特效
- `WithdrawCodeDialog` — 提现兑换码弹窗

**P1 动画清单**（后续添加）：
- `CoinArrivedWithProgressDialog`, `RunningEvent*`, `Spin*`, `TrainingPackage`, `WebView*`, `DailyCourseGift`, `SmashEggEntrance`, `SlotEntrance`

### 11.3 字体

**来源**：`/Users/he/Downloads/vitalo/Vitalo/Resources/Fonts/Inter-BlackItalic.otf`
**目标**：`app/src/main/res/font/inter_black_italic.otf`

### 11.4 本地化

**来源**：`/Users/he/Downloads/vitalo/Vitalo/Resources/Localizable.xcstrings` (JSON 格式)

**迁移步骤**：
1. 解析 JSON 文件
2. 提取每种语言的 key-value 对
3. 生成 `res/values/strings.xml`（默认英文）
4. 生成 `res/values-{locale}/strings.xml`（各语言）

---

## 12. 附录 D：风险与注意事项

### 12.1 加密兼容性（关键）

- iOS 使用 `CommonCrypto` 的 DES-ECB-**PKCS7Padding**
- Android 使用 `javax.crypto` 的 DES/ECB/**PKCS5Padding**
- PKCS5 和 PKCS7 对于 8 字节块（DES）行为完全一致，**可直接互通**
- **密钥预处理**必须精确对齐 `decodeKeyIfNeed` 逻辑：
  - Training DES Key `qEXOutWnp54` 需要 `isKeyEncoded = true`（先补齐长度到 4 的倍数，再 URL-safe Base64 → 标准 Base64 解码，截取/填充到 8 字节）
  - AccountCenter DES Key `O5iZQwpg` 也是 `isKeyEncoded = true`
  - ABTest DES Key `H7SDYH9X` 是 `isKeyEncoded = false`（直接 UTF-8 转字节，正好 8 字节）
- **强烈建议**：在 Phase 1 写 DES 工具类后，立刻用 iOS 的已知明文/密文对做单元测试验证

### 12.2 签名顺序

- iOS 签名时 query 参数用 `Dictionary.map { ... }.joined(separator: "&")`，**遍历顺序不确定**
- 如果后端也不排序（按 Java HashMap），则 Android 侧也用 `joinToString` 无排序即可
- 如果后端排序了，需要对 query keys **字母排序**后再拼接
- **建议**：先用 Android 无排序测试，如果签名验证失败再加排序

### 12.3 响应解密差异

- Training 域：响应体是 **URL-safe Base64 字符串** → 解码 → DES 解密
- AccountCenter / Coin / Game 域：响应体是 **原始 DES 密文 bytes** → 直接 DES 解密（不经过 Base64）
- ABTest 域：始终执行 URL-safe Base64 → DES 解密
- 必须按域创建不同的 Interceptor 或通过参数化区分

### 12.4 Google Maps

- 需要在 Google Cloud Console 开通 Maps SDK for Android
- 获取 API Key 后配置到 `AndroidManifest.xml`：
  ```xml
  <meta-data
      android:name="com.google.android.geo.API_KEY"
      android:value="YOUR_API_KEY" />
  ```

### 12.5 Android 特有权限处理

- `ACCESS_BACKGROUND_LOCATION` 必须单独请求（不能和前台定位一起请求）
- `ACTIVITY_RECOGNITION` API 29+ 才需要运行时请求
- `POST_NOTIFICATIONS` API 33+ 才需要
- 建议使用 Accompanist Permissions 库简化请求流程

### 12.6 数据模型注意

- iOS 的 `AutoLoginResult` 中 `bindingAccounts` 是 `Int`（不是数组）
- iOS 的 `Token.expiredIn` 是秒数（不是时间戳），表示从保存时刻起多少秒后过期
- iOS 的 `RunningRecord.startTime` / `endTime` 是 Unix timestamp（秒）

---

## 13. 附录 E: iOS 源文件路径速查

| 用途 | iOS 路径 |
|------|---------|
| 应用入口 | `Vitalo/VitaloApp.swift` |
| 根 UI 与路由表 | `Vitalo/ContentView.swift` |
| 路由枚举 | `Vitalo/Services/Route.swift` |
| 首页 | `Vitalo/Views/Home/HomeView.swift` |
| 自定义 TabBar | `Vitalo/Views/Home/CustomTabBar.swift` |
| 课程页 | `Vitalo/Views/Lesson/LessonView.swift` |
| 课程详情 | `Vitalo/Views/LessonDetail/LessonDetailView.swift` |
| 跟练页 | `Vitalo/Views/FollowAlong/FollowAlongView.swift` |
| 跑步页 | `Vitalo/Views/Running/RunningView.swift` |
| 任务页 | `Vitalo/Views/Task/TaskView.swift` |
| 兑换页 | `Vitalo/Views/Exchange/ExchangeView.swift` |
| 收集页 | `Vitalo/Views/Collection/CollectionView.swift` |
| 签到 | `Vitalo/Views/SignIn/SignInView.swift` |
| 个人中心 | `Vitalo/Views/Mine/MineView.swift` |
| 设置 | `Vitalo/Views/Settings/SettingsView.swift` |
| 最近活动 | `Vitalo/Views/RecentActivities/RecentActivitiesView.swift` |
| 步数 | `Vitalo/Views/StepCounter/StepCounterView.swift` |
| 转盘 | `Vitalo/Views/SpinWheel/SpinWheelView.swift` |
| WebView | `Vitalo/Views/Web/WebView.swift` |
| 新手引导 | `Vitalo/Views/BeginneGuide/BeginnerGuideView.swift` |
| 权限引导 | `Vitalo/Views/Permission/LocationPermissionView.swift` |
| 网络基类 | `Vitalo/Services/Network/BaseNetworkService.swift` |
| 网络协议 | `Vitalo/Services/Network/Protocols/*.swift` |
| Training 策略 | `Vitalo/Services/Network/Strategies/Training/*.swift` |
| AccountCenter 策略 | `Vitalo/Services/Network/Strategies/AccountCenter/*.swift` |
| Coin 策略 | `Vitalo/Services/Network/Strategies/Coin/*.swift` |
| ABTest 策略 | `Vitalo/Services/Network/Strategies/ABTest/*.swift` |
| Training 常量 | `Vitalo/Constants/TrainingConstants.swift` |
| AccountCenter 常量 | `Vitalo/Constants/AccountCenterConstants.swift` |
| Coin 常量 | `Vitalo/Constants/CoinConstants.swift` |
| Game 常量 | `Vitalo/Constants/GameConstants.swift` |
| ABTest 常量 | `Vitalo/Constants/ABTestContants.swift` |
| DES 加密 | `Vitalo/Services/Network/Utils/DESEncryptor.swift` |
| HMAC 签名 | `Vitalo/Services/Network/Utils/HMACSigner.swift` |
| 数据库管理 | `Vitalo/Services/DatabaseManager.swift` |
| 登录管理 | `Vitalo/Services/LoginManager.swift` |
| 用户管理 | `Vitalo/Models/UserManager.swift` |
| UserDefaults | `Vitalo/Utils/AppUserDefaults.swift` |
| CommonResponse | `Vitalo/Models/API/CommonResponse.swift` |
| AutoLoginResult | `Vitalo/Models/API/AccountCenter/AutoLoginResult.swift` |
| Token | `Vitalo/Models/API/AccountCenter/Token.swift` |
| Training 模型 | `Vitalo/Models/API/Training/Training.swift` |
| Lesson 模型 | `Vitalo/Models/API/Training/Lesson.swift` |
| Repository | `Vitalo/Services/Repository/*.swift` |
| Factory | `Vitalo/Services/Network/Strategies/*/...ServiceFactory.swift` |
| AlamofireProvider | `Vitalo/Services/Network/AlamofireProvider.swift` |
| Lottie 组件 | `Vitalo/Views/Common/CommonLottieView.swift` |
| 颜色扩展 | `Vitalo/Extensions/ColorExtension.swift` |
| 本地化 | `Vitalo/Resources/Localizable.xcstrings` |
| Lottie 资源 | `Vitalo/Resources/Lottie/` |
| 图片资源 | `Vitalo/Assets.xcassets/` |
| 字体 | `Vitalo/Resources/Fonts/Inter-BlackItalic.otf` |

---

## 使用方式

每个 Phase 开一个新的 Cursor 窗口，在对话中：

```
请按照 @MIGRATION_PLAN.md 的 Phase N 开始实施。
iOS 源码路径：/Users/he/Downloads/vitalo/
```

Agent 会根据本文档的任务清单、代码模板和 iOS 源文件路径，逐步完成迁移。
