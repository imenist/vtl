package com.vitalo.markrun.navigation

sealed class Screen(val route: String) {
    data object Splash : Screen("splash")
    data object BeginnerGuide : Screen("beginner_guide")
    data object Gender : Screen("gender")
    data object Birthday : Screen("birthday")
    data object Weight : Screen("weight")
    data object Height : Screen("height")
    data object Home : Screen("home")
    data object LessonDetail : Screen("lesson_detail/{trainingCode}/{partIndex}/{fromToday}") {
        fun createRoute(trainingCode: String, partIndex: Int, fromToday: Boolean) =
            "lesson_detail/$trainingCode/$partIndex/$fromToday"
    }
    data object ActionDetail : Screen("action_detail/{actionCode}")
    data object RunTracker : Screen("run_tracker")
    data object FollowAlong : Screen("follow_along/{trainingCode}/{lessonCode}")
    data object LocationPermission : Screen("location_permission")
    data object RunningResult : Screen("running_result/{recordId}/{fragmentsCount}")
    data object WorkoutResult : Screen("workout_result")
    data object Mine : Screen("mine")
    data object Settings : Screen("settings")
    data object RecentActivities : Screen("recent_activities")
    data object RunningDetail : Screen("running_detail/{recordId}")
    data object WebGame : Screen("web_game/{kind}")
    data object SpinWheel : Screen("spin_wheel")
    data object WithdrawRecord : Screen("withdraw_record")
}
