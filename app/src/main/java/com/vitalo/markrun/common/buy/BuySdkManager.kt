package com.vitalo.markrun.common.buy

import com.vitalo.markrun.common.statistic.StatSdkManger
import android.app.Application
import android.os.SystemClock
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.appsflyer.AppsFlyerLib
import com.cs.bd.buychannel.BuyChannelApi
import com.cs.bd.buychannel.BuySdkInitParams
import com.cs.bd.buychannel.buyChannel.bean.BuyChannelBean
import com.vitalo.markrun.VitaloApp
import com.vitalo.markrun.AppConst
import com.vitalo.markrun.AppStateManager
import com.vitalo.markrun.common.ab.AbConfigDataRepo
import com.vitalo.markrun.common.statistic.bean.StatConst
import com.vitalo.markrun.config.AppConfig
import com.vitalo.markrun.util.DebugLogContainer
import com.vitalo.markrun.util.LogUtils
import com.vitalo.markrun.util.MmkvUtils
import kotlin.math.ceil
import com.cs.bd.commerce.util.LogUtils as CLogUtil
    // Removed base http imports
import com.cs.bd.buychannel.AppsFlyerProxy
import com.cs.bd.buychannel.BuyChannelDataMgr
import com.vitalo.markrun.BuildConfig
import org.json.JSONObject


/**
 * @Date: 2026/3/19
 **/

object BuySdkManager {

    val buyCheckLiveData = MutableLiveData<BuyChannelBean?>()
    var hasBuyInfo = false
    var firstReceiveBuyInfo = false
    var buyUserReceiveCostTime = 0L
    var buyLaunchTime = 0L

    /**
     * 仅限于本地测试用的
     */
    val testUserInfo = BuyChannelBean().apply {
        secondUserType = 1
        buyChannel = "1.0-firebase-230111-old-ww"
        channelFrom = "ADV"
        firstUserType = "userbuy"
        campaign = MmkvUtils.getString(key_campaign) ?: "old"
    }

    const val key_campaign = "key_campaign"
    private val campaignList = listOf(
        Campaign.CART,
    )

    fun initSdk(context: Application) {
        val startTime = SystemClock.elapsedRealtime()
        buyLaunchTime = startTime
        StatSdkManger.upload104(
            opCode = StatConst.t000_start_play,
            location = if (AppStateManager.isFirstEnterApp()) "1" else "2"
        )
        
        val builder = BuySdkInitParams.Builder(
            AppConfig.statChannelId.toString(),
            AppConfig.functionId45,
            AppConfig.elephantProdId.toString(), {},
            false,
            AppConfig.elephantProdKey,
            AppConfig.elephantAccessKey
        )
        CLogUtil.setShowLog(true)
        builder.isApkUpLoad45(true)
        builder.upLoad45Imediately(true)
        BuyChannelApi.preInit(false, context)

        val hasRecognize = MmkvUtils.getBool(AppConst.HAS_RECEIVE_BUY_INFO)
        if (!hasRecognize) {
            firstReceiveBuyInfo = true
        }
        BuyChannelApi.init(context, builder.build())
        BuyChannelApi.registerBuyChannelListener(context) {
            val costTimeFloat = (SystemClock.elapsedRealtime() - buyLaunchTime) / 1000f
            val formattedCostTime = String.format(java.util.Locale.US, "%.1f", costTimeFloat)

            LogUtils.log("BuySdkManager", "===> userInfo:${it}")
            DebugLogContainer.putPayInfoLogs("买量回调--> $it")
            val appsFlyerId = AppsFlyerLib.getInstance().getAppsFlyerUID(context)

            if (!hasRecognize) {
                val costTime = ceil((SystemClock.elapsedRealtime() - startTime) / 1000f).toLong()
                buyUserReceiveCostTime = costTime
                MmkvUtils.putBoolean(AppConst.HAS_RECEIVE_BUY_INFO, true)
                uploadStat(costTime, it.isNotEmpty())

                if (!isBuyUser()) {
                    getAfGcd(retry = true, costTime) {
                        hasBuyInfo = true
                        buyCheckLiveData.postValue(getUserInfo())
                        AbConfigDataRepo.refreshRemoteData(true)
                    }
                    return@registerBuyChannelListener
                }
            }
            hasBuyInfo = true
            buyCheckLiveData.postValue(getUserInfo())
            LogUtils.log("BuySdkManager", "开始刷新AB...")
            AbConfigDataRepo.refreshRemoteData(true)
        }

        if (AppStateManager.isFirstEnterApp()) {
            StatSdkManger.upload104(StatConst.START_BUY_REQUEST)
        }
    }

    private fun uploadStat(costTime: Long, success: Boolean) {
        if (success) {
            StatSdkManger.upload104(
                StatConst.HP_SUS_TIME,
                "1",
                location = costTime.toString()
            )
        } else {
            StatSdkManger.upload104(
                StatConst.HP_SUS_TIME,
                "2",
                location = costTime.toString()
            )
        }
    }

    fun getUserInfo(): BuyChannelBean {
        if (BuildConfig.DEBUG ) {
            return testUserInfo
        }
        return BuyChannelApi.getBuyChannelBean(VitaloApp.getInstance())
    }

    fun isBuyUser(): Boolean {
        if (BuildConfig.DEBUG  && getCampaign() != Campaign.EMPTY) {
            return true
        }

        val userInfo = getUserInfo()
        return userInfo.isUserBuy
    }

    fun getBuyChannel(): String {
        return getUserInfo().buyChannel ?: ""
    }

    fun getUserFrom(): Int {
        return getUserInfo().secondUserType
    }

    fun getCampaign(): Campaign {

//        if (BuildConfig.DEBUG) {
//            val debugCampaign = MmkvUtils.getString(key_campaign) ?: ""
//            if (debugCampaign.isNotEmpty()) {
//                return Campaign.values().find { it.value == debugCampaign } ?: Campaign.EMPTY
//            }
//        }

        val campaign = getUserInfo().campaign ?: ""
        if (campaign.isEmpty()) return Campaign.EMPTY
        for (ca in campaignList) {
            if (campaign.contains(ca.value)) {
                return ca
            }
        }
        return Campaign.EMPTY
    }


    private fun getAfGcd(retry: Boolean = false, costTime: Long, callBack: (() -> Unit)? = null) {
        val context = VitaloApp.getInstance()
        val deviceId = AppsFlyerLib.getInstance().getAppsFlyerUID(context)
        val gcdStartTime = SystemClock.elapsedRealtime()
        StatSdkManger.upload104(
            opCode = StatConst.begin_buy_af_gcdreq,
            statObj = costTime.toString(),
            entrance = if (retry) "1" else "2"
        )

        val client = okhttp3.OkHttpClient()
        val url = "https://gcdsdk.appsflyer.com/install_data/v4.0/${com.vitalo.markrun.BuildConfig.APPLICATION_ID}?devkey=${AppConfig.afDevKey}&device_id=$deviceId"
        val request = okhttp3.Request.Builder()
            .url(url)
            .addHeader("accept", "application/json")
            .build()

        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                try {
                    val bodyStr = response.body?.string()
                    LogUtils.log("BuySdkManager", "AF GCD Response -> code:${response.code} body:$bodyStr")

                    val jsonObject = JSONObject(bodyStr ?: "{}")
                    val afStatus = jsonObject.optString("af_status")
                    if (afStatus == "Non-organic") {
                        val afString = jsonObject.toString()
                        val sp = BuyChannelDataMgr.getInstance(context).getSharedPreferences(context)
                        sp.edit().putString("appflyer_data", afString).commit()
                        val appsFlyerProxy = AppsFlyerProxy.getInstance(context)
                        val data = appsFlyerProxy.getData(afString)
                        val method = appsFlyerProxy.javaClass.getDeclaredMethod("analysisAfData", Map::class.java)
                        method.isAccessible = true
                        method.invoke(appsFlyerProxy, data)
                    } else if (afStatus == "Organic" && retry) {
                        Thread.sleep(12000)
                        val gcdCostTime = ceil((SystemClock.elapsedRealtime() - gcdStartTime) / 1000f).toLong()
                        getAfGcd(retry = false, costTime = costTime + gcdCostTime, callBack)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    StatSdkManger.upload104(
                        opCode = StatConst.begin_buy_af_gcdtype,
                        tab = getUserFrom().toString(),
                        statObj = ceil((SystemClock.elapsedRealtime() - gcdStartTime) / 1000f).toLong().toString(),
                        entrance = if (retry) "1" else "2"
                    )
                    callBack?.invoke()
                }
            }

            override fun onFailure(call: okhttp3.Call, e: java.io.IOException) {
                LogUtils.log("BuySdkManager", "AF GCD onError -> ${e.message}")
                callBack?.invoke()
            }
        })
    }

}

enum class Campaign(val value: String) {
    CART("cart"),
    EMPTY("")
}
