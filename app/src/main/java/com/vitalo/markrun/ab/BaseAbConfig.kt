package com.vitalo.markrun.ab

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

/**
 * 所有 AB 实验配置 bean 的基类。
 * 服务端每条 cfg 都会携带 cfg_id。
 */
@Keep
open class BaseAbConfig {

    @SerializedName("cfg_id")
    var cfgId: Int? = null
}
