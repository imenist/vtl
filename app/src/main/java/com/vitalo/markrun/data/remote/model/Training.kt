package com.vitalo.markrun.data.remote.model

import com.google.gson.annotations.SerializedName

data class Subject(
    @SerializedName("subject_id") val subjectId: Int?,
    val name: String,
    val label: List<Int>?,
    val training: List<Training>?,
    val style: String?
)

data class SubjectListPage(
    val subject: List<Subject>?,
    val cursor: Int?
)

data class Training(
    val code: String,
    val name: String?,
    val type: Int?,
    val cover: String?,
    @SerializedName("cover_size") val coverSize: String?,
    val video: String?,
    @SerializedName("duration_type") val durationType: Int?,
    val duration: Int?,
    val calorie: Double?,
    val parts: List<Part>?
)

data class Part(
    val name: String?,
    val lessons: List<PartLesson>?
)

data class PartLesson(
    val code: String,
    val name: String?,
    @SerializedName("cover_url") val coverUrl: String?,
    val duration: Int?,
    val calories: Double?
)

data class Lesson(
    val code: String,
    val name: String?,
    val cover: String?,
    @SerializedName("cover_size") val coverSize: String?,
    @SerializedName("overview_cover") val overviewCover: String?,
    @SerializedName("overview_video") val overviewVideo: String?,
    @SerializedName("cover_tablet") val coverTablet: String?,
    @SerializedName("cover_size_tablet") val coverSizeTablet: String?,
    @SerializedName("overview_cover_tablet") val overviewCoverTablet: String?,
    @SerializedName("overview_video_tablet") val overviewVideoTablet: String?,
    val duration: Int?,
    val calorie: Double?,
    val introduction: String?,
    val label: List<Int>?,
    val level: Int?,
    val target: List<Int>?,
    @SerializedName("training_area") val trainingArea: List<Int>?,
    val pay: Int?,
    @SerializedName("mount_actions") val mountActions: List<MountAction>?
)

data class MountAction(
    val part: Int?,
    @SerializedName("preview_duration") val previewDuration: Int?,
    val duration: Int?,
    @SerializedName("voice_coverage") val voiceCoverage: List<VoiceCoverage>?,
    val action: Action?
)

data class VoiceCoverage(
    @SerializedName("customize_prompt") val customizePrompt: String?,
    @SerializedName("prompt_time_point") val promptTimePoint: Int?,
    val speed: Double?
)

data class Action(
    val code: String,
    val name: String?,
    val thumbnail: String?,
    @SerializedName("thumbnail_size") val thumbnailSize: String?,
    val videos: List<Video>?,
    @SerializedName("countdown_time_point") val countdownTimePoint: Int?,
    @SerializedName("countdown_prompt") val countdownPrompt: String?,
    @SerializedName("preview_prompt") val previewPrompt: String?,
    @SerializedName("preview_prompt_mp3") val previewPromptMp3: String?,
    val calorie: Double?,
    val steps: String?,
    val label: List<String>?,
    val level: Int?,
    @SerializedName("training_area") val trainingArea: List<String>?,
    @SerializedName("common_mistake") val commonMistake: String?,
    val feel: String?,
    val breathing: String?,
    val good: String?,
    @SerializedName("voice_coach_txt") val voiceCoachTxt: String?,
    @SerializedName("voice_coach_mp3") val voiceCoachMp3: String?
)

data class Video(
    @SerializedName("camera_angle") val cameraAngle: Int?,
    @SerializedName("first_frame") val firstFrame: String?,
    @SerializedName("video_url") val videoUrl: String?
)

data class Plan(
    val code: String,
    val name: String?,
    val type: Int?,
    val cover: String?,
    @SerializedName("cover_size") val coverSize: String?,
    @SerializedName("overview_cover") val overviewCover: String?,
    @SerializedName("overview_video") val overviewVideo: String?,
    @SerializedName("cover_tablet") val coverTablet: String?,
    @SerializedName("cover_size_tablet") val coverSizeTablet: String?,
    @SerializedName("overview_cover_tablet") val overviewCoverTablet: String?,
    @SerializedName("overview_video_tablet") val overviewVideoTablet: String?,
    val days: Int?,
    val calorie: Double?,
    val introduction: String?,
    val label: List<Int>?,
    val level: Int?,
    val target: List<Int>?,
    @SerializedName("training_area") val trainingArea: List<Int>?,
    @SerializedName("mount_lessons") val mountLessons: List<MountLesson>?
)

data class MountLesson(
    val day: Int?,
    val stage: Int?,
    val copywriting: String?,
    val lesson: Lesson?
)

data class Album(
    val code: String,
    val name: String?,
    val icon: String?,
    @SerializedName("icon_size") val iconSize: String?,
    val cover: String?,
    @SerializedName("cover_size") val coverSize: String?,
    @SerializedName("lesson_num") val lessonNum: Int?,
    val lessons: List<Lesson>?
)
