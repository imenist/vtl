package com.vitalo.markrun.common.ab.impl

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.vitalo.markrun.common.ab.BaseAbConfig

/**
 * @Date: 2026/3/20
 **/

class WithDrawConfig(
    @SerializedName("out_switch")
    val outSwitch: String? = "0",  //1-开 0-关
    @SerializedName("out_json")
    val outJson: String,
    @SerializedName("top_coin")
    val topCoin: String? = "0",  //1-开 0-关
    @SerializedName("drama_ball")
    val dramaBall: String? = "0",  //1-开 0-关
    @SerializedName("task_wall")
    val taskWall: String? = "0",  //1-开 0-关
): BaseAbConfig() {

    fun getParsedOutJson(): OutJsonData? {
        if (outJson.isBlank()) return null
        return try {
            Gson().fromJson(outJson, OutJsonData::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}

data class OutJsonData(
    @SerializedName("list")
    val list: List<OutJsonItem>? = null
)

data class OutJsonItem(
    @SerializedName("out_pr_num")
    val outPrNum: String? = null,
    @SerializedName("out_pr_unit")
    val outPrUnit: String? = null
)