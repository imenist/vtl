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
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        val mapsApiKey: String = project.findProperty("MAPS_API_KEY") as? String ?: ""
        manifestPlaceholders["MAPS_API_KEY"] = mapsApiKey
    }

    signingConfigs {
        create("releaseKey") {
            storeFile = file("../key/markrun")
            storePassword = "markrun_gp"
            keyAlias = "markrun_gp"
            keyPassword = "markrun_gp"
        }
    }

    flavorDimensions += "env"
    productFlavors {
        create("dev") {
            dimension = "env"
            applicationId = "com.markrun.dev"
            versionNameSuffix = "-dev"
            resValue("string", "app_name", "MarkRun Dev")
        }
        create("product") {
            dimension = "env"
            applicationId = "com.markrun.app"
            resValue("string", "app_name", "MarkRun")
        }
    }

    buildFeatures {
        buildConfig = true
    }

    buildTypes {
        release {
            signingConfig = signingConfigs.getByName("releaseKey")
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

    implementation("com.tencent:mmkv:2.4.0")
    //统计sdk
    implementation(files("libs/statistics-com.markrun.app-release-v2.3.5.aar"))
    // commerce sdk（文件在 app/libs/，路径与文件名需与实际一致）
    implementation(files("libs/BuyChannelSdk-trunk-v1.10.1-r245619-AF-6.5.2.aar")) // 买量
    implementation(files("libs/CommerceUtilsSdk-V1.10.0-release-svn242295.aar"))
    implementation(files("libs/custom_ab_sdk.jar")) //ab
    implementation("commons-codec:commons-codec:1.15")
    implementation("com.appsflyer:af-android-sdk:6.13.0")
    implementation("com.appsflyer:adrevenue:6.9.1")
}
