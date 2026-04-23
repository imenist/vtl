package com.vitalo.markrun.ad

import android.app.Activity
import android.content.Context
import android.util.Log
import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxAdListener
import com.applovin.mediation.MaxError
import com.applovin.mediation.MaxReward
import com.applovin.mediation.MaxRewardedAdListener
import com.applovin.mediation.ads.MaxInterstitialAd
import com.applovin.mediation.ads.MaxRewardedAd
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.vitalo.markrun.common.ab.impl.AdConfig
import com.vitalo.markrun.common.ab.AbConfigDataRepo
import com.vitalo.markrun.common.ab.AbSidTable
import com.vitalo.markrun.data.remote.model.AdModuleItem
import com.vitalo.markrun.util.MmkvUtils
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.vitalo.markrun.data.remote.api.NewStoreApi
import com.vitalo.markrun.data.remote.model.NewStoreAdModuleBody
import com.vitalo.markrun.data.remote.model.NewStoreDeviceInfo
import com.vitalo.markrun.common.http.HttpClient

object AdManager {
    private const val TAG = "AdManager"

    // 记录AdModuleResponse缓存
    private val newStoreCache = mutableMapOf<Int, AdModuleItem>()

    // 缓存加载的广告，避免重复加载
    private val maxInterstitialAds = mutableMapOf<String, MaxInterstitialAd>()
    private val maxRewardedAds = mutableMapOf<String, MaxRewardedAd>()

    private val admobInterstitialAds = mutableMapOf<String, InterstitialAd>()
    private val admobRewardedAds = mutableMapOf<String, RewardedAd>()
    
    // 是否正在加载中，避免重复请求 (主要针对AdMob)
    private val loadingAds = mutableSetOf<String>()

    // 定义广告源类型常量
    object AdSource {
        const val ADMOB = 8
        const val APPLOVIN = 20
        const val APPLOVIN_TWO = 50 // 这个直接当作APPLOVIN看待,服务器会有两个APPLOVIN类型
    }

    // 定义广告类型常量
    object AdType {
        const val BANNER = 1                 // banner图
        const val FULL_SCREEN = 2            // 全屏
        const val ICON = 3                   // icon
        const val VIDEO = 4                  // 视频
        const val BANNER_300_250 = 5         // banner(300*250)
        const val NATIVE = 6                 // 信息流-native
        const val INTERSTITIAL_VIDEO = 7     // 插屏视频
        const val SPLASH = 8                 // 开屏广告
        const val NATIVE_BANNER = 9          // banner(原生)
        const val FEED_TEMPLATE = 10         // 信息流模板
        const val DRAW_FEED = 11             // draw信息流
        const val REWARD_VIDEO_2_0 = 12      // 激励视频2.0
        const val INTERSTITIAL_FULL = 13     // 插全屏(穿山甲聚合)
        const val REWARD_INTERSTITIAL = 14   // 插页式激励视频
    }

    /**
     * 预加载广告
     */
    fun preloadAd(context: Context, virtualId: Int) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val adModuleItem = getAdModuleItem(context, virtualId)
                if (adModuleItem == null || adModuleItem.adId.isNullOrEmpty()) return@launch

                val adUnitId = adModuleItem.adId!!
                val adSource = adModuleItem.adSource ?: AdSource.APPLOVIN
                val adType = adModuleItem.adType ?: AdType.REWARD_VIDEO_2_0

                if (adSource == AdSource.ADMOB) {
                    if (isInterstitial(adType)) {
                        loadAdMobInterstitial(context, adUnitId)
                    } else {
                        loadAdMobRewarded(context, adUnitId)
                    }
                } else if (adSource == AdSource.APPLOVIN || adSource == AdSource.APPLOVIN_TWO) {
                    if (context is Activity) {
                        if (isInterstitial(adType)) {
                            loadMaxInterstitial(context, adUnitId)
                        } else {
                            loadMaxRewarded(context, adUnitId)
                        }
                    } else {
                        Log.w(TAG, "AppLovin 预加载需要 Activity Context")
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "预加载异常: ${e.message}")
            }
        }
    }

    /**
     * 测试用：直接展示测试广告
     */
    fun showTestAd(
        activity: Activity,
        adSource: Int,
        adType: Int,
        onAdClosed: (() -> Unit)? = null,
        onReward: (() -> Unit)? = null
    ) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                // 使用官方测试ID
                val adUnitId = if (adSource == AdSource.ADMOB) {
                    if (isInterstitial(adType)) {
                        "ca-app-pub-3940256099942544/1033173712" // AdMob Interstitial Test ID
                    } else {
                        "ca-app-pub-3940256099942544/5224354917" // AdMob Rewarded Test ID
                    }
                } else {
                    // AppLovin 的测试通过后台配置设备或统一的Test ID处理，这里直接写入您实际可能用来测试的虚拟ID或保留空白
                    // Max 的测试模式一般需要在SDK初始化或后台设置，如果没有特殊的测试ID，就用个默认或者提示
                    Log.w(TAG, "AppLovin 暂无统一测试ID，请使用后台配置的测试设备")
                    // 这里为了防止崩溃，随便塞个占位，或者你需要具体的测试ID可以替换
                    if (isInterstitial(adType)) {
                        "YOUR_MAX_INTERSTITIAL_TEST_ID" 
                    } else {
                        "YOUR_MAX_REWARDED_TEST_ID"
                    }
                }

                // 强制设置虚拟Id为0用于测试记录
                val virtualId = 0

                if (adSource == AdSource.ADMOB) {
                    if (isInterstitial(adType)) {
                        showAdMobInterstitial(activity, virtualId, adUnitId, onAdClosed)
                    } else {
                        showAdMobRewarded(activity, virtualId, adUnitId, onAdClosed, onReward)
                    }
                } else if (adSource == AdSource.APPLOVIN || adSource == AdSource.APPLOVIN_TWO) {
                    if (isInterstitial(adType)) {
                        showMaxInterstitial(activity, virtualId, adUnitId, onAdClosed)
                    } else {
                        showMaxRewarded(activity, virtualId, adUnitId, onAdClosed, onReward)
                    }
                } else {
                    Log.e(TAG, "未知的广告源: adSource=$adSource")
                    onAdClosed?.invoke()
                }
            } catch (e: Exception) {
                Log.e(TAG, "测试广告展示异常: ${e.message}")
                onAdClosed?.invoke()
            }
        }
    }

    /**
     * 展示广告
     */
    fun showAd(
        activity: Activity,
        virtualId: Int,
        onAdClosed: (() -> Unit)? = null,
        onReward: (() -> Unit)? = null
    ) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                withTimeout(30_000L) {
                    // 1. 获取对应的 AdConfig，检查 AB 实验或各种限制（频率，次数等）
                    val adConfig = getAdConfig(virtualId)
                    if (!checkAdIfAllowed(virtualId, adConfig)) {
                        Log.d(TAG, "广告被AB配置拦截或达到上限: virtualId=$virtualId")
                        onAdClosed?.invoke()
                        return@withTimeout
                    }

                    // 2. 获取服务端下发的广告配置
                    val adModuleItem = getAdModuleItem(activity, virtualId)
                    if (adModuleItem == null || adModuleItem.adId.isNullOrEmpty()) {
                        Log.e(TAG, "获取不到广告配置或adId为空: virtualId=$virtualId")
                        onAdClosed?.invoke()
                        return@withTimeout
                    }

                    val adUnitId = adModuleItem.adId!!
                    val adSource = adModuleItem.adSource ?: AdSource.APPLOVIN
                    val adType = adModuleItem.adType ?: AdType.REWARD_VIDEO_2_0

                    if (adSource == AdSource.ADMOB) {
                        if (isInterstitial(adType)) {
                            showAdMobInterstitial(activity, virtualId, adUnitId, onAdClosed)
                        } else {
                            showAdMobRewarded(activity, virtualId, adUnitId, onAdClosed, onReward)
                        }
                    } else if (adSource == AdSource.APPLOVIN || adSource == AdSource.APPLOVIN_TWO) {
                        if (isInterstitial(adType)) {
                            showMaxInterstitial(activity, virtualId, adUnitId, onAdClosed)
                        } else {
                            showMaxRewarded(activity, virtualId, adUnitId, onAdClosed, onReward)
                        }
                    } else {
                        Log.e(TAG, "未知的广告源: adSource=$adSource")
                        onAdClosed?.invoke()
                    }
                }
            } catch (e: TimeoutCancellationException) {
                Log.e(TAG, "广告加载超时30s: virtualId=$virtualId")
                onAdClosed?.invoke()
            } catch (e: Exception) {
                Log.e(TAG, "广告展示异常: ${e.message}")
                onAdClosed?.invoke()
            }
        }
    }

    private fun isInterstitial(adType: Int): Boolean {
        return adType == AdType.INTERSTITIAL_VIDEO || 
               adType == AdType.FULL_SCREEN || 
               adType == AdType.INTERSTITIAL_FULL ||
               adType == AdType.SPLASH
    }

    /* =======================================================
     *                      AdMob 逻辑
     * ======================================================= */

    private fun loadAdMobInterstitial(context: Context, adUnitId: String) {
        if (admobInterstitialAds[adUnitId] != null || loadingAds.contains(adUnitId)) return
        loadingAds.add(adUnitId)
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(context, adUnitId, adRequest, object : InterstitialAdLoadCallback() {
            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                admobInterstitialAds[adUnitId] = interstitialAd
                loadingAds.remove(adUnitId)
                Log.d(TAG, "AdMob 插屏广告预加载成功: $adUnitId")
            }
            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                loadingAds.remove(adUnitId)
                Log.e(TAG, "AdMob 插屏广告预加载失败: ${loadAdError.message}")
            }
        })
    }

    private suspend fun showAdMobInterstitial(activity: Activity, virtualId: Int, adUnitId: String, onAdClosed: (() -> Unit)?) {
        val cachedAd = admobInterstitialAds[adUnitId]
        if (cachedAd != null) {
            displayAdMobInterstitial(activity, virtualId, adUnitId, cachedAd, onAdClosed)
        } else {
            val loadResult = CompletableDeferred<InterstitialAd?>()
            val adRequest = AdRequest.Builder().build()
            InterstitialAd.load(activity, adUnitId, adRequest, object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    loadResult.complete(interstitialAd)
                }
                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    Log.e(TAG, "AdMob 插屏广告加载失败: ${loadAdError.message}")
                    loadResult.complete(null)
                }
            })
            val ad = loadResult.await()
            if (ad != null) {
                displayAdMobInterstitial(activity, virtualId, adUnitId, ad, onAdClosed)
            } else {
                onAdClosed?.invoke()
            }
        }
    }

    private fun displayAdMobInterstitial(activity: Activity, virtualId: Int, adUnitId: String, ad: InterstitialAd, onAdClosed: (() -> Unit)?) {
        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                admobInterstitialAds.remove(adUnitId)
                onAdClosed?.invoke()
            }
            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                Log.e(TAG, "AdMob 插屏展示失败: ${adError.message}")
                admobInterstitialAds.remove(adUnitId)
                onAdClosed?.invoke()
            }
            override fun onAdShowedFullScreenContent() {
                recordAdShown(virtualId)
            }
        }
        ad.show(activity)
    }

    private fun loadAdMobRewarded(context: Context, adUnitId: String) {
        if (admobRewardedAds[adUnitId] != null || loadingAds.contains(adUnitId)) return
        loadingAds.add(adUnitId)
        val adRequest = AdRequest.Builder().build()
        RewardedAd.load(context, adUnitId, adRequest, object : RewardedAdLoadCallback() {
            override fun onAdLoaded(rewardedAd: RewardedAd) {
                admobRewardedAds[adUnitId] = rewardedAd
                loadingAds.remove(adUnitId)
                Log.d(TAG, "AdMob 激励视频预加载成功: $adUnitId")
            }
            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                loadingAds.remove(adUnitId)
                Log.e(TAG, "AdMob 激励视频预加载失败: ${loadAdError.message}")
            }
        })
    }

    private suspend fun showAdMobRewarded(activity: Activity, virtualId: Int, adUnitId: String, onAdClosed: (() -> Unit)?, onReward: (() -> Unit)?) {
        val cachedAd = admobRewardedAds[adUnitId]
        if (cachedAd != null) {
            displayAdMobRewarded(activity, virtualId, adUnitId, cachedAd, onAdClosed, onReward)
        } else {
            val loadResult = CompletableDeferred<RewardedAd?>()
            val adRequest = AdRequest.Builder().build()
            RewardedAd.load(activity, adUnitId, adRequest, object : RewardedAdLoadCallback() {
                override fun onAdLoaded(rewardedAd: RewardedAd) {
                    loadResult.complete(rewardedAd)
                }
                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    Log.e(TAG, "AdMob 激励视频加载失败: ${loadAdError.message}")
                    loadResult.complete(null)
                }
            })
            val ad = loadResult.await()
            if (ad != null) {
                displayAdMobRewarded(activity, virtualId, adUnitId, ad, onAdClosed, onReward)
            } else {
                onAdClosed?.invoke()
            }
        }
    }

    private fun displayAdMobRewarded(activity: Activity, virtualId: Int, adUnitId: String, ad: RewardedAd, onAdClosed: (() -> Unit)?, onReward: (() -> Unit)?) {
        var isRewarded = false
        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                admobRewardedAds.remove(adUnitId)
                if (isRewarded) {
                    onReward?.invoke()
                }
                onAdClosed?.invoke()
            }
            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                Log.e(TAG, "AdMob 激励视频展示失败: ${adError.message}")
                admobRewardedAds.remove(adUnitId)
                onAdClosed?.invoke()
            }
            override fun onAdShowedFullScreenContent() {
                recordAdShown(virtualId)
            }
        }
        ad.show(activity) { _ ->
            isRewarded = true
        }
    }

    /* =======================================================
     *                   AppLovin MAX 逻辑
     * ======================================================= */

    private fun loadMaxInterstitial(activity: Activity, adUnitId: String) {
        var interstitialAd = maxInterstitialAds[adUnitId]
        if (interstitialAd == null) {
            interstitialAd = MaxInterstitialAd(adUnitId, activity)
            maxInterstitialAds[adUnitId] = interstitialAd
        }
        if (!interstitialAd!!.isReady) {
            interstitialAd.setListener(object : MaxAdListener {
                override fun onAdLoaded(ad: MaxAd) { Log.d(TAG, "AppLovin 插屏加载成功: $adUnitId") }
                override fun onAdLoadFailed(adUnitId: String, error: MaxError) { Log.e(TAG, "AppLovin 插屏加载失败: ${error.message}") }
                override fun onAdDisplayed(ad: MaxAd) {}
                override fun onAdHidden(ad: MaxAd) {}
                override fun onAdClicked(ad: MaxAd) {}
                override fun onAdDisplayFailed(ad: MaxAd, error: MaxError) {}
            })
            interstitialAd.loadAd()
        }
    }

    private suspend fun showMaxInterstitial(activity: Activity, virtualId: Int, adUnitId: String, onAdClosed: (() -> Unit)?) {
        var interstitialAd = maxInterstitialAds[adUnitId]
        if (interstitialAd == null) {
            interstitialAd = MaxInterstitialAd(adUnitId, activity)
            maxInterstitialAds[adUnitId] = interstitialAd
        }

        if (interstitialAd!!.isReady) {
            displayMaxInterstitial(activity, virtualId, interstitialAd, onAdClosed)
        } else {
            val loadResult = CompletableDeferred<Boolean>()
            interstitialAd.setListener(object : MaxAdListener {
                override fun onAdLoaded(ad: MaxAd) {
                    if (!loadResult.isCompleted) loadResult.complete(true)
                }
                override fun onAdLoadFailed(adUnitId: String, error: MaxError) {
                    Log.e(TAG, "AppLovin 插屏加载失败: ${error.message}")
                    if (!loadResult.isCompleted) loadResult.complete(false)
                }
                override fun onAdDisplayed(ad: MaxAd) {}
                override fun onAdHidden(ad: MaxAd) {}
                override fun onAdClicked(ad: MaxAd) {}
                override fun onAdDisplayFailed(ad: MaxAd, error: MaxError) {}
            })
            interstitialAd.loadAd()
            val success = loadResult.await()
            if (success) {
                displayMaxInterstitial(activity, virtualId, interstitialAd, onAdClosed)
            } else {
                onAdClosed?.invoke()
            }
        }
    }

    private fun displayMaxInterstitial(activity: Activity, virtualId: Int, ad: MaxInterstitialAd, onAdClosed: (() -> Unit)?) {
        ad.setListener(object : MaxAdListener {
            override fun onAdLoaded(maxAd: MaxAd) {}
            override fun onAdLoadFailed(adUnitId: String, error: MaxError) {}
            override fun onAdDisplayed(maxAd: MaxAd) {
                recordAdShown(virtualId)
            }
            override fun onAdHidden(maxAd: MaxAd) {
                onAdClosed?.invoke()
            }
            override fun onAdClicked(maxAd: MaxAd) {}
            override fun onAdDisplayFailed(maxAd: MaxAd, error: MaxError) {
                Log.e(TAG, "AppLovin 插屏展示失败: ${error.message}")
                onAdClosed?.invoke()
            }
        })
        ad.showAd()
    }

    private fun loadMaxRewarded(activity: Activity, adUnitId: String) {
        var rewardedAd = maxRewardedAds[adUnitId]
        if (rewardedAd == null) {
            rewardedAd = MaxRewardedAd.getInstance(adUnitId, activity)
            maxRewardedAds[adUnitId] = rewardedAd
        }
        if (!rewardedAd!!.isReady) {
            rewardedAd.setListener(object : MaxRewardedAdListener {
                override fun onAdLoaded(ad: MaxAd) { Log.d(TAG, "AppLovin 激励加载成功: $adUnitId") }
                override fun onAdLoadFailed(adUnitId: String, error: MaxError) { Log.e(TAG, "AppLovin 激励加载失败: ${error.message}") }
                override fun onAdDisplayed(ad: MaxAd) {}
                override fun onAdHidden(ad: MaxAd) {}
                override fun onAdClicked(ad: MaxAd) {}
                override fun onAdDisplayFailed(ad: MaxAd, error: MaxError) {}
                override fun onUserRewarded(ad: MaxAd, reward: MaxReward) {}
            })
            rewardedAd.loadAd()
        }
    }

    private suspend fun showMaxRewarded(activity: Activity, virtualId: Int, adUnitId: String, onAdClosed: (() -> Unit)?, onReward: (() -> Unit)?) {
        var rewardedAd = maxRewardedAds[adUnitId]
        if (rewardedAd == null) {
            rewardedAd = MaxRewardedAd.getInstance(adUnitId, activity)
            maxRewardedAds[adUnitId] = rewardedAd
        }

        if (rewardedAd!!.isReady) {
            displayMaxRewarded(activity, virtualId, rewardedAd, onAdClosed, onReward)
        } else {
            val loadResult = CompletableDeferred<Boolean>()
            rewardedAd.setListener(object : MaxRewardedAdListener {
                override fun onAdLoaded(ad: MaxAd) {
                    if (!loadResult.isCompleted) loadResult.complete(true)
                }
                override fun onAdLoadFailed(adUnitId: String, error: MaxError) {
                    Log.e(TAG, "AppLovin 激励加载失败: ${error.message}")
                    if (!loadResult.isCompleted) loadResult.complete(false)
                }
                override fun onAdDisplayed(ad: MaxAd) {}
                override fun onAdHidden(ad: MaxAd) {}
                override fun onAdClicked(ad: MaxAd) {}
                override fun onAdDisplayFailed(ad: MaxAd, error: MaxError) {}
                override fun onUserRewarded(ad: MaxAd, reward: MaxReward) {}
            })
            rewardedAd.loadAd()
            val success = loadResult.await()
            if (success) {
                displayMaxRewarded(activity, virtualId, rewardedAd, onAdClosed, onReward)
            } else {
                onAdClosed?.invoke()
            }
        }
    }

    private fun displayMaxRewarded(activity: Activity, virtualId: Int, ad: MaxRewardedAd, onAdClosed: (() -> Unit)?, onReward: (() -> Unit)?) {
        var isRewarded = false
        ad.setListener(object : MaxRewardedAdListener {
            override fun onAdLoaded(maxAd: MaxAd) {}
            override fun onAdLoadFailed(adUnitId: String, error: MaxError) {}
            override fun onAdDisplayed(maxAd: MaxAd) {
                recordAdShown(virtualId)
            }
            override fun onAdHidden(maxAd: MaxAd) {
                if (isRewarded) {
                    onReward?.invoke()
                }
                onAdClosed?.invoke()
            }
            override fun onAdClicked(maxAd: MaxAd) {}
            override fun onAdDisplayFailed(maxAd: MaxAd, error: MaxError) {
                Log.e(TAG, "AppLovin 激励展示失败: ${error.message}")
                onAdClosed?.invoke()
            }
            override fun onUserRewarded(maxAd: MaxAd, reward: MaxReward) {
                isRewarded = true
            }
        })
        ad.showAd()
    }

    /* =======================================================
     *                   公共校验与数据逻辑
     * ======================================================= */

    /**
     * 针对普通广告的常规AB判断检查：开关、频率限制、上限限制
     */
    private fun checkAdIfAllowed(virtualId: Int, adConfig: AdConfig?): Boolean {
        adConfig ?: return true
        
        // 1. 检查开关
        if (!adConfig.isOpen()) {
            Log.d(TAG, "广告开关关闭")
            return false
        }
        
        val currentDate = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())
        val countKey = "ad_count_${virtualId}_$currentDate"
        val lastTimeKey = "ad_last_time_$virtualId"
        
        // 2. 检查每日上限
        val currentCount = MmkvUtils.getInt(countKey, 0)
        if (currentCount >= adConfig.adNumLimit) {
            Log.d(TAG, "广告达到每日上限: $currentCount >= ${adConfig.adNumLimit}")
            return false
        }
        
        // 3. 检查广告间隔 (ads_lag单位是秒)
        val lastTime = MmkvUtils.getLong(lastTimeKey, 0L)
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastTime < adConfig.adIntervals * 1000L) {
            Log.d(TAG, "广告未达到间隔时间: 间隔需 ${adConfig.adIntervals} 秒")
            return false
        }
        
        return true
    }

    /**
     * 在广告成功展示后调用，记录广告位本身的展示次数和时间
     */
    private fun recordAdShown(virtualId: Int) {
        val currentDate = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())
        val countKey = "ad_count_${virtualId}_$currentDate"
        val lastTimeKey = "ad_last_time_$virtualId"
        
        val currentCount = MmkvUtils.getInt(countKey, 0)
        MmkvUtils.putInt(countKey, currentCount + 1)
        MmkvUtils.putLong(lastTimeKey, System.currentTimeMillis())
    }

    private suspend fun getAdModuleItem(context: Context, virtualId: Int): AdModuleItem? {
        // 先看有没有缓存
        newStoreCache[virtualId]?.let {
            // 复制一个对象，设置virtualId用于后续判断类型
            val item = it.copy()
            item.virtualId = virtualId
            return item
        }

        return withContext(Dispatchers.IO) {
            try {
                val service = HttpClient.createService(NewStoreApi::class.java)
                val body = NewStoreAdModuleBody(
                    device = NewStoreDeviceInfo.create(context),
                    virtualId = virtualId,
                    keywords = null
                )
                val response = service.getAdModule(body)
                
                if (response.errorCode == 200 || response.errorCode == 0) {
                    val pModule = response.data?.pModule
                    val cModuleFirst = response.data?.cModule?.firstOrNull()
                    val resultItem = cModuleFirst ?: pModule
                    
                    if (resultItem != null) {
                        newStoreCache[virtualId] = resultItem
                        // 设置返回对象的virtualId
                        resultItem.virtualId = virtualId
                    }
                    resultItem
                } else {
                    Log.e(TAG, "Request failed: ${response.errorMessage}")
                    null
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e(TAG, "Exception: ${e.message}")
                null
            }
        }
    }

    private fun getAdConfig(virtualId: Int): AdConfig? {
        @Suppress("UNCHECKED_CAST")
        val configs = AbConfigDataRepo.getCurrentConfigs(AbSidTable.AD) as? List<AdConfig>
        return configs?.find { it.virtualId == virtualId }
    }
}
