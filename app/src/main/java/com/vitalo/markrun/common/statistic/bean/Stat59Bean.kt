package com.vitalo.markrun.common.statistic.bean

import android.provider.Settings
import com.cs.statistic.StatisticsManager
import com.cs.statistic.utiltool.Machine
import com.cs.statistic.utiltool.UtilTool
import com.vitalo.markrun.VitaloApp
import com.vitalo.markrun.config.AppConfig
import com.vitalo.markrun.common.statistic.StatSdkManger

class Stat59Bean(val opCode: String) : BaseStatBean() {
    private var opResult: String? = null
    private var orderType = "2"
    private var remark1: String? = null
    private var remark2: String? = null

    init {
        tab = "0"
    }

    fun skuName(sku: String?): Stat59Bean {
        statObj = sku
        return this
    }

    fun orderId(orderId: String?): Stat59Bean {
        location = orderId
        return this
    }

    fun orderType(subType: Boolean): Stat59Bean {
        orderType = if (subType) "2" else "1"
        return this
    }

    fun entry59(entry59: String?): Stat59Bean {
        entry = entry59
        return this
    }

    fun result(result: String): Stat59Bean {
        opResult = result
        return this
    }

    fun remark1(remark: String): Stat59Bean {
        remark1 = remark
        return this
    }

    fun remark2(remark: String): Stat59Bean {
        remark2 = remark
        return this
    }

    fun uploadBySdk() {
        StatSdkManger.upload59(this)
    }

    override fun getLogId(): String {
        return StatConst.PROTOCOL_ID_59.toString()
    }

    override fun getFunId(): String {
        return AppConfig.packageName
    }

    override fun convertUploadString(): String {
        return StringBuilder().apply {
            append(getLogId())  //日志序列 1
            append(StatConst.SEPARATOR)
            append(Settings.Secure.getString(VitaloApp.getInstance().contentResolver, Settings.Secure.ANDROID_ID)) //唯一标识 2
            append(StatConst.SEPARATOR)
            append(UtilTool.getBeiJinTime(System.currentTimeMillis())) //打印时间 3
            append(StatConst.SEPARATOR)
            append(getFunId())  // 产品包名 4
            append(StatConst.SEPARATOR)
            append(statObj) // 统计对象 5
            append(StatConst.SEPARATOR)
            append(opCode) // 操作代码 6
            append(StatConst.SEPARATOR)
            append(opResult) // 操作结果 7
            append(StatConst.SEPARATOR)
            append(Machine.getSimCountryIso(VitaloApp.getInstance(), true))  // 国家 8
            append(StatConst.SEPARATOR)
            append(AppConfig.statChannelId)  // 渠道 9
            append(StatConst.SEPARATOR)
            append(AppConfig.versionCode) // 版本号 10
            append(StatConst.SEPARATOR)
            append(AppConfig.versionName) // 版本名 11
            append(StatConst.SEPARATOR)
            append(entry) // 入口 12
            append(StatConst.SEPARATOR)
            append(tab) // 第三方付费渠道 13
            append(StatConst.SEPARATOR)
            append(location) // 订单id 14
            append(StatConst.SEPARATOR)
            append("0") // 第三方支付上传服务器的accountId 15
            append(StatConst.SEPARATOR)
            append(StatisticsManager.getUserId(VitaloApp.getInstance())) // goid 16
            append(StatConst.SEPARATOR)
            append("0") // 第三方订单需要上传金额:订单 => 5.99:CNY   17
            append(StatConst.SEPARATOR)
            append(mark) // ios支付使用  18
            append(StatConst.SEPARATOR)
            append(orderType) // 订单类型 19
            append(StatConst.SEPARATOR)
            append(StatisticsManager.getGoogleAdID(VitaloApp.getInstance())) // GAID 20
            append(StatConst.SEPARATOR)
            append(remark1) // 备注1
            append(StatConst.SEPARATOR)
            append(remark2) // 备注2
        }.toString()
    }

    object ResultType {
        const val NEUTRAL = "0"
        const val OK = "1"
    }

    object OpType {
        const val PAGE_SHOW = "f000"
        const val PAY_ACTION = "j005"
        const val PAY_SUCCESS = "p001"
    }
}
