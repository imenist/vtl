package com.vitalo.markrun.common.statistic.bean

import java.lang.StringBuilder

abstract class BaseStatBean {
    var code: String? = null
        get() {
            return if (field == null) "" else field
        }

    var entry: String? = null
        get() {
            return if (field == null) "" else field
        }

    var location: String? = null
        get() {
            return if (field == null) "" else field
        }

    var tab: String? = null
        get() {
            return if (field == null) "" else field
        }

    var statObj: String? = null
        get() {
            return if (field == null) "" else field
        }

    var mark: String? = null
        get() {
            return if (field == null) "" else field
        }

    var associatedObj: String? = null
        get() {
            return if (field == null) "" else field
        }

    var result: String = "1"

    var adId: String = ""

    abstract fun getLogId(): String
    abstract fun getFunId(): String

    open fun convertUploadString(): String {
        return java.lang.StringBuilder().apply {
            append(getFunId())
            append(StatConst.SEPARATOR)
            append(statObj)
            append(StatConst.SEPARATOR)
            append(code)
            append(StatConst.SEPARATOR)
            append(result)
            append(StatConst.SEPARATOR)
            append(entry)
            append(StatConst.SEPARATOR)
            append(tab)
            append(StatConst.SEPARATOR)
            append(location)
            append(StatConst.SEPARATOR)
            append(associatedObj)
            append(StatConst.SEPARATOR)
            append(adId) // 广告ID位置
            append(StatConst.SEPARATOR)
            append(mark)
        }.toString()
    }

    override fun toString(): String {
        return """
            code -> $code, FunId -> ${getFunId()}, statObj -> $statObj, result -> $result, entry -> $entry, tab -> $tab, location -> $location, associatedObj -> $associatedObj, adId -> $adId, mark -> $mark
        """.trimIndent()
    }
}
