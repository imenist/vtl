package com.vitalo.markrun.common.statistic.bean

import com.vitalo.markrun.config.AppConfig

class Stat19Bean: BaseStatBean() {
    override fun getLogId(): String {
        return "19"
    }

    override fun getFunId(): String {
        return AppConfig.functionId19.toString()
    }
}
