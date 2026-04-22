package com.vitalo.markrun.common.ab

import com.google.gson.*
import java.lang.Exception
import java.lang.reflect.Type

/**
 * AbConfig返回数据解析器
 * 为了适配多个子类，采用自定义解析
 */
class AbResultParser(private val contracts: List<AbConfigContract>): JsonDeserializer<Map<String, AbConfigResponse.Data>>{

    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): Map<String, AbConfigResponse.Data> {
        val result = mutableMapOf<String, AbConfigResponse.Data>()
        contracts.forEach {
            val infoKey = if (contracts.size == 1) {
                "infos"
            }else {
                getSidKey(it.sid)
            }
            val infoObj = json?.asJsonObject?.get(infoKey)?.asJsonObject
            val abData = AbConfigResponse.Data().apply {
                filterId = infoObj?.get(KEY_FILTER_ID)?.asInt
                abtestId = infoObj?.get(KEY_ABTEST_ID)?.asInt
            }
            val cfgArray = infoObj?.get(KEY_CFGS)?.asJsonArray
            val baseConfig = mutableListOf<BaseAbConfig>()
            cfgArray?.forEach { inner ->
                val cfgObj = inner.asJsonObject
                context?.deserialize<BaseAbConfig>(cfgObj, it.type.java)?.apply {
                    baseConfig.add(this)
                }
            }
            abData.cfgs = baseConfig
            result[getSidKey(it.sid)] = abData
        }
        return result
    }

    companion object {
        const val KEY_INFO = "infos_"
        const val KEY_FILTER_ID = "filter_id"
        const val KEY_ABTEST_ID = "abtest_id"
        const val KEY_CFGS = "cfgs"

        fun getSidKey(sid: Int): String{
            return KEY_INFO + sid
        }

        fun createGson(contracts: List<AbConfigContract>): Gson {
            val deserializer = AbResultParser(contracts)
            return AbResultParserJavaHelper.createGson(deserializer)
        }

        fun extract(json: String?, contracts: List<AbConfigContract>): AbConfigResponse? {
            if (json.isNullOrEmpty()) {
                return null
            }
            return try {
                createGson(contracts).fromJson(json, AbConfigResponse::class.java)
            }catch (ex: Exception) {
                null
            }
        }
    }
}