package com.vitalo.markrun.common.ab

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
open class BaseAbConfig {

    @SerializedName("cfg_id")
    var cfgId: Int? = null

}
