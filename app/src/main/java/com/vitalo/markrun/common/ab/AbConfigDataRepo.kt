package com.vitalo.markrun.common.ab

import android.annotation.SuppressLint
import android.content.Context
import android.os.SystemClock
import android.util.Log
import com.cpcphone.abtestcenter.AbtestCenterService
import com.cs.bd.commerce.util.Machine
import com.google.gson.Gson
import com.vitalo.markrun.VitaloApp
import com.vitalo.markrun.AppStateManager
import com.vitalo.markrun.BuildConfig
import com.vitalo.markrun.common.ab.impl.BonusConfig
import com.vitalo.markrun.common.ab.impl.WithDrawConfig
import com.vitalo.markrun.common.ab.repo.JsonCacheDataRepo
import com.vitalo.markrun.common.buy.BuySdkManager
import com.vitalo.markrun.common.statistic.StatSdkManger
import com.vitalo.markrun.common.statistic.bean.StatConst
import com.vitalo.markrun.config.AppConfig
import com.vitalo.markrun.util.LogUtils
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import java.lang.reflect.Type
import kotlin.toString

/**
 * Ab业务请求管理
 * 基于8小时缓存策略以及定制化 Gson 反序列化
 */
@SuppressLint("StaticFieldLeak")
object AbConfigDataRepo : JsonCacheDataRepo<AbConfigResponse>(VitaloApp.getInstance()) {

    private val contracts = mutableListOf<AbConfigContract>()

    // 保存原始 JSON 字符串，用于调试查看
    private var rawJsonCache: String? = null

    init {
        // 在这里添加你在 AbSidTable 中定义的 AB 实验对象
        contracts.add(AbSidTable.KCAL_LIMIT)
        contracts.add(AbSidTable.NEW_USER_SPIN)
        contracts.add(AbSidTable.SIGN_REWARD)
        contracts.add(AbSidTable.HAMMER)
        contracts.add(AbSidTable.SLOT_MACHINE)
        contracts.add(AbSidTable.FLOP_COIN)
        contracts.add(AbSidTable.TASK_REWARD)
        contracts.add(AbSidTable.MINI_GAME_SWITCH)
        contracts.add(AbSidTable.DAILY_GUIDE)
        contracts.add(AbSidTable.AD)
        contracts.add(AbSidTable.AD_POLICY)
        contracts.add(AbSidTable.WITHDRAW_ENABLE)
        contracts.add(AbSidTable.WITHDRAW_GRADE)
        contracts.add(AbSidTable.H5_TASK_AD)
        contracts.add(AbSidTable.APP_UI_SWITCH)
        
        contracts.add(AbSidTable.BONUS)
        contracts.add(AbSidTable.WITHDRAW)
    }

    override fun createDeserializerGson(): Gson {
        return AbResultParser.createGson(contracts)
    }

    override fun getCacheSaveDir(): String {
        // 使用应用内部缓存目录
        return context.cacheDir.absolutePath + "/ab_data"
    }

    override fun getDataTypeToken(): Type {
        return AbConfigResponse::class.java
    }

    override fun getRepoIdentityKey(): String {
        return "ab_request_data"
    }

    private val okHttpClient by lazy { 
        okhttp3.OkHttpClient.Builder()
            .addInterceptor(com.vitalo.markrun.data.remote.interceptor.ABTestInterceptor())
            .build() 
    }

    override fun fetchRemoteData(callback: (remoteData: AbConfigResponse?, success: Boolean) -> Unit) {
        val abTestRequest = AbConfigDataRepo.buildAbRequestParams(context)
        val startTime = SystemClock.elapsedRealtime()
        val userFrom = BuySdkManager.getUserFrom().toString()
        StatSdkManger.upload104(
            opCode = StatConst.t000_start_ab_request,
            tab = userFrom
        )
        try {
            Log.d(logTag, "开始请求AB, AB请求地址:${abTestRequest.getUrl("")}")
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

        abTestRequest.send(object : AbtestCenterService.ResultCallback {
            override fun onResponse(json: String?) {
                val costTimeFloat = (SystemClock.elapsedRealtime() - startTime) / 1000f
                val formattedCostTime = String.format(java.util.Locale.US, "%.1f", costTimeFloat)
                AbConfigDataRepo.logLongJson(json)
                Log.d(logTag, "拉取ab完成")
                AbConfigDataRepo.rawJsonCache = json
                val response = AbResultParser.extract(json,
                    AbConfigDataRepo.contracts
                )
                if (response == null) {
                    Log.d(logTag, "解析json失败, 请检查配置bean")
                    StatSdkManger.upload104(
                        opCode = StatConst.t000_start_ab_result,
                        statObj = formattedCostTime,
                        entrance = "2_parse_error",
                        tab = userFrom
                    )
                    callback(null, false)
                } else {
                    Log.d(logTag, "解析json成功...")
                    val abIds = response.datas?.values?.mapNotNull { it.abtestId }?.joinToString(",") ?: ""
                    StatSdkManger.upload104(
                        opCode = StatConst.t000_start_ab_result,
                        statObj = formattedCostTime,
                        entrance = "1_$abIds",
                        tab = userFrom
                    )
                    callback(response, true)
                    saveRemoteDataToCache(response, json)
                }
            }

            override fun onError(p0: String?, p1: Int) {
                Log.d(logTag, "拉取ab失败:$p0")
                val costTimeFloat = (SystemClock.elapsedRealtime() - startTime) / 1000f
                val formattedCostTime = String.format(java.util.Locale.US, "%.1f", costTimeFloat)
                StatSdkManger.upload104(
                    opCode = StatConst.t000_start_ab_result,
                    statObj = formattedCostTime,
                    entrance = "2_$p0",
                    tab = userFrom
                )
                callback(null, false)
            }
        })
    }

    /**
     * 获取AB请求的配置信息
     */
    private fun buildAbRequestParams(context: Context): AbtestCenterService {
        AbtestCenterService.enableLog(true)
        return AbtestCenterService.Builder()
            .accessKey(AppConfig.abTestSecretKey)
            .productKey(AppConfig.abTestProductKey)
            .sid(getSidArray())
            .cid(AppConfig.abTestCid)
            .cid2(AppConfig.abTestCid2)
            .cversion(AppConfig.versionCode) // version code
            .local(java.util.Locale.getDefault().country)
            .utm_source(BuySdkManager.getBuyChannel())
            .user_from(BuySdkManager.getUserFrom())
            .entrance(
                if (BuildConfig.FLAVOR == "dev") {
                    AbtestCenterService.Builder.Entrance.TEST
                } else {
                    AbtestCenterService.Builder.Entrance.MAIN_PACKAGE
                })
            .cdays(AppStateManager.getAppInstallDay())
            .isupgrade(if (AppStateManager.isUpgradeUser()) 1 else 2)
            .aid(Machine.getAndroidId(context))
            .channel(AppConfig.statChannelId)
            .isSafe(true)
            .build(context)
    }

    private fun getSidArray(): IntArray {
        val sidArray = IntArray(contracts.size)
        for (index in contracts.indices) {
            sidArray[index] = contracts[index].sid
        }
        return sidArray
    }

    fun getCurrentResponse(): AbConfigResponse? {
        return getObservableData().value
    }

    fun getRawJsonCache(): String? {
        return rawJsonCache
    }

    fun getCurrentConfigs(contract: AbConfigContract): List<BaseAbConfig>? {
        val dataKey = AbResultParser.getSidKey(contract.sid)
        return getCurrentResponse()?.datas?.get(dataKey)?.cfgs
    }

    fun getCurrentConfig(contract: AbConfigContract): BaseAbConfig? {
        return getCurrentConfigs(contract)?.firstOrNull()
    }

    fun AbConfigResponse.firstConfig(contract: AbConfigContract): BaseAbConfig? {
        return allConfigs(contract)?.firstOrNull()
    }

    fun AbConfigResponse.allConfigs(contract: AbConfigContract): List<BaseAbConfig>? {
        val dataKey = AbResultParser.getSidKey(contract.sid)
        return this.datas?.get(dataKey)?.cfgs
    }

    private val defaultWithDrawJson = """
        {
          "list": [
            {
              "out_pr_num": "1000,2000,3000,4000",
              "out_pr_unit": "$"
            }
          ]
        }
    """.trimIndent()

    private val defaultBonusJson = """
        {
          "global": {
            "target_amount": 1000,
            "ad_multiplier": 2
          },
          "f_factor_rules": [
            {
              "gap_percent_min": 80,
              "f_min": 0.4,
              "f_max": 0.6
            },
            {
              "gap_percent_min": 50,
              "f_min": 0.08,
              "f_max": 0.12
            },
            {
              "gap_percent_min": 20,
              "f_min": 0.02,
              "f_max": 0.03
            },
            {
              "gap_percent_min": 5,
              "f_min": 0.004,
              "f_max": 0.006
            },
            {
              "gap_percent_min": 0,
              "f_min_formula": "gap * 0.000005",
              "f_max_formula": "gap * 0.000015"
            }
          ],
          "base_rewards": {
            "bubble": 1,
            "watch_time": [
              {
                "minutes": 10,
                "reward": 3
              },
              {
                "minutes": 20,
                "reward": 5
              },
              {
                "minutes": 30,
                "reward": 7
              },
              {
                "minutes": 40,
                "reward": 9
              },
              {
                "minutes": 50,
                "reward": 12
              },
              {
                "minutes": 60,
                "reward": 15
              }
            ],
            "watch_episodes": [
              {
                "episodes": 3,
                "reward": 2
              },
              {
                "episodes": 8,
                "reward": 4
              },
              {
                "episodes": 13,
                "reward": 6
              },
              {
                "episodes": 18,
                "reward": 8
              },
              {
                "episodes": 23,
                "reward": 11
              },
              {
                "episodes": 28,
                "reward": 15
              }
            ],
            "sign_in": [
              {
                "day": 1,
                "reward": 10
              },
              {
                "day": 2,
                "reward": 15
              },
              {
                "day": 3,
                "reward": 20
              },
              {
                "day": 4,
                "reward": 10
              },
              {
                "day": 5,
                "reward": 15
              },
              {
                "day": 6,
                "reward": 25
              },
              {
                "day": 7,
                "reward": 80
              }
            ],
            "direct_ad": {
              "reward": 4,
              "daily_limit": 30
            }
          }
        }
    """.trimIndent()

    fun getWithDrawConfig(): WithDrawConfig {
        val config = getCurrentConfig(AbSidTable.WITHDRAW) as? WithDrawConfig
        if (config != null && !config.outJson.isNullOrBlank()) {
            return config
        }
        return WithDrawConfig(outSwitch = "0", outJson = defaultWithDrawJson)
    }

    fun getBonusConfig(): BonusConfig {
        val config = getCurrentConfig(AbSidTable.BONUS) as? BonusConfig
        if (config != null && config.sceNumJson.isNotBlank()) {
            return config
        }
        return BonusConfig(sceNumJson = defaultBonusJson)
    }

    /**
     * 分段输出长JSON
     */
    private fun logLongJson(json: String?) {
        if (json == null) return

        val maxLogSize = 3000 // 每段最大长度
        val stringLength = json.length

        if (stringLength <= maxLogSize) {
            LogUtils.log(logTag, "拉取ab完成:$json")
        } else {
            LogUtils.log(logTag, "拉取ab完成(长度:$stringLength)，开始分段输出:")
            var i = 0
            while (i < stringLength) {
                val end = minOf(i + maxLogSize, stringLength)
                val segment = json.substring(i, end)
                LogUtils.log(logTag, segment)
                i = end
            }
            LogUtils.log(logTag, "AB-JSON分段输出完成")
        }
    }
}