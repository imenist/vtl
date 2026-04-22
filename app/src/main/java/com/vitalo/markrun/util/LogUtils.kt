package com.vitalo.markrun.util

import android.util.Log
import com.vitalo.markrun.BuildConfig

/**
 * 日志工具类
 */
object LogUtils {
    var OPEN = BuildConfig.DEBUG

    fun setOpen(open: Boolean) {
        OPEN = open
    }

    fun log(tag: String, msg: String) {
        if (OPEN) {
            Log.d(tag, msg)
            DebugLogContainer.putLogUtilLogs(msg)
        }
    }

    fun err(tag: String, msg: String) {
        if (OPEN) {
            Log.e(tag, msg)
            DebugLogContainer.putLogUtilLogs(msg)
        }
    }

    fun err(tag: String, t: Throwable) {
        if (OPEN) {
            Log.e(tag, t.stackTraceToString())
        }
    }

    fun traceHere(tag: String) {
        if (OPEN) {
            Log.e(tag, Throwable("Who call me ======>").stackTraceToString())
        }
    }

    fun log(msg: String) {
        if (OPEN) {
            log("LogUtils", msg)
            DebugLogContainer.putLogUtilLogs(msg)
        }
    }

    fun log(current: Any, msg: String) {
        log(current::class.java.simpleName, msg)
    }
}

fun Any.logDebug(tag: String, msg: String) {
    LogUtils.log(tag, msg)
}

fun Any.logError(tag: String, msg: String) {
    LogUtils.err(tag, msg)
}

fun Any.logDebug(current: Any, msg: String) {
    if (!LogUtils.OPEN) {
        return
    }
    val tag = current::class.java.simpleName
    LogUtils.log(tag, msg)
}
