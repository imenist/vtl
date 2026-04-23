package com.vitalo.markrun.ab.impl

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import com.vitalo.markrun.common.ab.BaseAbConfig

/**
 * 通用 AB 配置 —— 当前项目暂时用一个通用 bean 承载所有实验字段。
 *
 * 后续为某个实验创建专属 Config 类时，只需继承 [BaseAbConfig]，
 * 并在 [com.vitalo.markrun.ab.AbSidTable] 中注册对应的 SID 即可。
 *
 * 字段说明（与服务端协商后按需增删）：
 * - switch       : 开关，"1" 表示开启, "0" 表示关闭
 * - jsonConfig   : 携带复杂配置的 JSON 字符串
 */
@Keep
class CommonAbConfig(
    @SerializedName("switch")
    val switch: String? = "0",

    @SerializedName("json_config")
    val jsonConfig: String? = null,
) : BaseAbConfig() {

    fun isEnabled(): Boolean = switch == "1"

    override fun toString(): String =
        "CommonAbConfig(cfgId=$cfgId, switch=$switch, jsonConfig=$jsonConfig)"
}
