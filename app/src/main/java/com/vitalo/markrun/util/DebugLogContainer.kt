package com.vitalo.markrun.util

import java.util.concurrent.CopyOnWriteArrayList

/**
 * 调试日志容器
 */
object DebugLogContainer {
    val OPEN = LogUtils.OPEN
    private val httpApiLogs = CopyOnWriteArrayList<String>()
    private val workflowLogs = CopyOnWriteArrayList<String>()
    private val payInfoLogs = CopyOnWriteArrayList<String>()
    private val adInfoLogs = CopyOnWriteArrayList<String>()
    private val logUtilLogs = CopyOnWriteArrayList<String>()

    fun putHttpLog(message: String) {
        if (!OPEN) {
            return
        }
        httpApiLogs.add(message)
    }

    fun putWorkflowLog(message: String) {
        if (!OPEN) {
            return
        }
        workflowLogs.add(message)
    }

    fun putPayInfoLogs(message: String) {
        if (!OPEN) {
            return
        }
        payInfoLogs.add(message)
    }

    fun putAdInfoLogs(message: String) {
        if (!OPEN) {
            return
        }
        adInfoLogs.add(message)
    }

    fun putLogUtilLogs(message: String) {
        if (!OPEN) {
            return
        }
        logUtilLogs.add(message)
    }

    fun getLogUtilLogs() = logUtilLogs
    fun getHttpApiLogs() = httpApiLogs
    fun getWorkflowLogs() = workflowLogs
    fun getPayInfoLogs() = payInfoLogs
    fun getAdInfoLogs() = adInfoLogs
}
