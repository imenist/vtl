package com.vitalo.markrun.common.ab.impl

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import com.vitalo.markrun.common.ab.BaseAbConfig

@Keep
class H5TaskAdConfig(
    @SerializedName("h5_ad_entry_link_control")
    val h5AdEntryLinkControl: String = "https://web.eswevents.com/ https://page.eswevents.com/ https://saman.luvfinetips.com/#/en/ https://pose.luvfinetips.com/#/en/ https://hootgameb.site/  https://n1.zonitascap.org https://n2.zonitascap.org https://moon.baoug.top/ https://cloud.baoug.top/ https://star.baoug.top/",
    @SerializedName("daily_task_h5_ad_link")
    val dailyTaskH5AdLink: String = "https://hootgameb.site/  https://n1.zonitascap.org https://n2.zonitascap.org https://moon.baoug.top/ https://cloud.baoug.top/ https://star.baoug.top/",
    @SerializedName("h5_ad_link_jump")
    val h5AdLinkJump: String = "1",
    @SerializedName("task_h5_ad_link_num")
    val taskH5AdLinkNum: Int = 3,
    @SerializedName("cfg_tb_id")
    val cfgTbId: Int = 0
) : BaseAbConfig()