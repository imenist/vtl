package com.vitalo.markrun.common.statistic.bean

import com.vitalo.markrun.config.AppConfig

class Stat104Bean: BaseStatBean() {
    override fun getLogId(): String {
        return "104"
    }

    override fun getFunId(): String {
        return AppConfig.functionId104.toString()
    }
}
