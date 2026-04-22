package com.vitalo.markrun.common.statistic.bean

import com.vitalo.markrun.config.AppConfig

class Stat105Bean: BaseStatBean() {
    override fun getLogId(): String {
        return "105"
    }

    override fun getFunId(): String {
        return AppConfig.functionId105.toString()
    }
}
