package com.vitalo.markrun.data.remote.api

import com.vitalo.markrun.data.remote.model.*
import retrofit2.http.Body
import retrofit2.http.POST

interface TrainingApi {
    @POST("/api/v1/subject")
    suspend fun fetchSubjectList(@Body payload: @JvmSuppressWildcards Map<String, Any>): CommonResponse<SubjectListPage>

    @POST("/api/v1/lesson")
    suspend fun fetchLessonDetail(@Body payload: @JvmSuppressWildcards Map<String, Any>): CommonResponse<Lesson>

    @POST("/api/v1/action")
    suspend fun fetchActionDetail(@Body payload: @JvmSuppressWildcards Map<String, Any>): CommonResponse<Action>

    @POST("/api/v1/recommend")
    suspend fun fetchRecommendPlan(@Body payload: @JvmSuppressWildcards Map<String, Any>): CommonResponse<Plan>

    @POST("/api/v1/plan")
    suspend fun fetchPlanDetail(@Body payload: @JvmSuppressWildcards Map<String, Any>): CommonResponse<Plan>

    @POST("/api/v1/album")
    suspend fun fetchAlbum(@Body payload: @JvmSuppressWildcards Map<String, Any>): CommonResponse<Album>
}
