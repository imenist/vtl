package com.vitalo.markrun.data.repository

import com.vitalo.markrun.data.remote.api.TrainingApi
import com.vitalo.markrun.data.remote.model.*
import com.vitalo.markrun.util.DeviceInfoUtils
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TrainingRepository @Inject constructor(
    private val api: TrainingApi,
    private val deviceInfoUtils: DeviceInfoUtils
) {
    suspend fun fetchSubjectList(cursor: Int = 0): CommonResponse<SubjectListPage> {
        val payload = mapOf<String, Any>(
            "device" to deviceInfoUtils.getDeviceInfoMap(),
            "cursor" to cursor
        )
        return api.fetchSubjectList(payload)
    }

    suspend fun fetchLessonDetail(code: String): CommonResponse<Lesson> {
        val payload = mapOf<String, Any>(
            "device" to deviceInfoUtils.getDeviceInfoMap(),
            "code" to code
        )
        return api.fetchLessonDetail(payload)
    }

    suspend fun fetchActionDetail(actionCode: String): CommonResponse<Action> {
        val payload = mapOf<String, Any>(
            "device" to deviceInfoUtils.getDeviceInfoMap(),
            "code" to actionCode
        )
        return api.fetchActionDetail(payload)
    }

    suspend fun fetchRecommendPlan(): CommonResponse<Plan> {
        val payload = mapOf<String, Any>(
            "device" to deviceInfoUtils.getDeviceInfoMap()
        )
        return api.fetchRecommendPlan(payload)
    }

    suspend fun fetchPlanDetail(planCode: String): CommonResponse<Plan> {
        val payload = mapOf<String, Any>(
            "device" to deviceInfoUtils.getDeviceInfoMap(),
            "code" to planCode
        )
        return api.fetchPlanDetail(payload)
    }

    suspend fun fetchAlbum(albumCode: String): CommonResponse<Album> {
        val payload = mapOf<String, Any>(
            "device" to deviceInfoUtils.getDeviceInfoMap(),
            "code" to albumCode
        )
        return api.fetchAlbum(payload)
    }
}
