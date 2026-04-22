package com.vitalo.markrun.ab

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

/**
 * 自定义 Gson 反序列化器。
 *
 * 因为 datas 里的每条 cfgs 需要根据 SID 对应的 Kotlin class 做多态解析，
 * 所以这里手动解析，而非依赖 Gson 的默认行为。
 */
class AbResultParser(
    private val contracts: List<AbConfigContract>
) : JsonDeserializer<Map<String, AbConfigResponse.Data>> {

    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): Map<String, AbConfigResponse.Data> {
        val result = mutableMapOf<String, AbConfigResponse.Data>()
        contracts.forEach { contract ->
            // 单 SID 时服务端 key 为 "infos"，多 SID 时为 "infos_<sid>"
            val infoKey = if (contracts.size == 1) "infos" else getSidKey(contract.sid)
            val infoObj = json?.asJsonObject?.get(infoKey)?.asJsonObject
            val abData = AbConfigResponse.Data().apply {
                filterId = infoObj?.get(KEY_FILTER_ID)?.asInt
                abtestId = infoObj?.get(KEY_ABTEST_ID)?.asInt
            }
            val cfgArray = infoObj?.get(KEY_CFGS)?.asJsonArray
            val cfgList = mutableListOf<BaseAbConfig>()
            cfgArray?.forEach { element ->
                context?.deserialize<BaseAbConfig>(element.asJsonObject, contract.type.java)
                    ?.let { cfgList.add(it) }
            }
            abData.cfgs = cfgList
            result[getSidKey(contract.sid)] = abData
        }
        return result
    }

    companion object {
        const val KEY_INFO_PREFIX = "infos_"
        const val KEY_FILTER_ID = "filter_id"
        const val KEY_ABTEST_ID = "abtest_id"
        const val KEY_CFGS = "cfgs"

        fun getSidKey(sid: Int): String = KEY_INFO_PREFIX + sid

        fun createGson(contracts: List<AbConfigContract>): Gson {
            val deserializer = AbResultParser(contracts)
            val type: Type = object : TypeToken<Map<String, AbConfigResponse.Data>>() {}.type
            return GsonBuilder()
                .registerTypeAdapter(type, deserializer)
                .create()
        }

        fun extract(json: String?, contracts: List<AbConfigContract>): AbConfigResponse? {
            if (json.isNullOrEmpty()) return null
            return try {
                createGson(contracts).fromJson(json, AbConfigResponse::class.java)
            } catch (_: Exception) {
                null
            }
        }
    }
}
