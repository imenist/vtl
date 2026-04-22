package com.vitalo.markrun.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.vitalo.markrun.ui.followalong.FollowAlongScreen
import com.vitalo.markrun.ui.followalong.WorkoutResultScreen
import com.vitalo.markrun.ui.home.HomeScreen
import com.vitalo.markrun.ui.lessondetail.ActionInstructionScreen
import com.vitalo.markrun.ui.lessondetail.LessonDetailScreen
import com.vitalo.markrun.ui.mine.MineScreen
import com.vitalo.markrun.ui.onboarding.BeginnerGuideScreen
import com.vitalo.markrun.ui.onboarding.BirthdaySelectionScreen
import com.vitalo.markrun.ui.onboarding.GenderSelectionScreen
import com.vitalo.markrun.ui.onboarding.HeightSelectionScreen
import com.vitalo.markrun.ui.onboarding.WeightSelectionScreen
import com.vitalo.markrun.ui.permission.LocationPermissionScreen
import com.vitalo.markrun.ui.recentactivities.RecentActivitiesScreen
import com.vitalo.markrun.ui.running.RunningDetailScreen
import com.vitalo.markrun.ui.running.RunningResultScreen
import com.vitalo.markrun.ui.running.RunningScreen
import com.vitalo.markrun.ui.exchange.WithdrawRecordScreen
import com.vitalo.markrun.ui.settings.SettingsScreen
import com.vitalo.markrun.ui.splash.SplashScreen
import com.vitalo.markrun.ui.spinwheel.SpinWheelScreen
import com.vitalo.markrun.ui.web.WebViewScreen

@Composable
fun NavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        // ─── Phase 1: Splash & Onboarding ───
        composable(Screen.Splash.route) {
            SplashScreen(navController = navController)
        }
        composable(Screen.BeginnerGuide.route) {
            BeginnerGuideScreen(navController = navController)
        }
        composable(Screen.Gender.route) {
            GenderSelectionScreen(navController = navController)
        }
        composable(Screen.Birthday.route) {
            BirthdaySelectionScreen(navController = navController)
        }
        composable(Screen.Height.route) {
            HeightSelectionScreen(navController = navController)
        }
        composable(Screen.Weight.route) {
            WeightSelectionScreen(navController = navController)
        }

        // ─── Phase 2: Home & Course ───
        composable(Screen.Home.route) {
            HomeScreen(navController = navController)
        }

        composable(
            route = Screen.LessonDetail.route,
            arguments = listOf(
                navArgument("trainingCode") { type = NavType.StringType },
                navArgument("partIndex") { type = NavType.IntType },
                navArgument("fromToday") { type = NavType.BoolType }
            )
        ) { backStackEntry ->
            LessonDetailScreen(
                navController = navController,
                trainingCode = backStackEntry.arguments?.getString("trainingCode") ?: "",
                partIndex = backStackEntry.arguments?.getInt("partIndex") ?: 1,
                fromToday = backStackEntry.arguments?.getBoolean("fromToday") ?: false
            )
        }

        composable(
            route = Screen.ActionDetail.route,
            arguments = listOf(
                navArgument("actionCode") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            ActionInstructionScreen(
                navController = navController,
                actionCode = backStackEntry.arguments?.getString("actionCode") ?: ""
            )
        }

        composable(
            route = Screen.FollowAlong.route,
            arguments = listOf(
                navArgument("trainingCode") { type = NavType.StringType },
                navArgument("lessonCode") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val trainingCode = backStackEntry.arguments?.getString("trainingCode") ?: ""
            val lessonCode = backStackEntry.arguments?.getString("lessonCode") ?: ""
            FollowAlongScreen(
                navController = navController,
                trainingCode = trainingCode,
                lessonCode = lessonCode,
                actions = emptyList()
            )
        }

        composable(Screen.WorkoutResult.route) {
            WorkoutResultScreen(navController = navController)
        }

        // ─── Phase 3: Running ───
        composable(Screen.LocationPermission.route) {
            LocationPermissionScreen(navController = navController)
        }

        composable(Screen.RunTracker.route) {
            RunningScreen(navController = navController)
        }

        composable(
            route = Screen.RunningResult.route,
            arguments = listOf(
                navArgument("recordId") { type = NavType.LongType },
                navArgument("fragmentsCount") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            RunningResultScreen(
                navController = navController,
                recordId = backStackEntry.arguments?.getLong("recordId") ?: 0L,
                fragmentsCount = backStackEntry.arguments?.getInt("fragmentsCount") ?: 0
            )
        }

        composable(
            route = Screen.RunningDetail.route,
            arguments = listOf(
                navArgument("recordId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            RunningDetailScreen(
                navController = navController,
                recordId = backStackEntry.arguments?.getLong("recordId") ?: 0L
            )
        }

        // ─── Phase 4: Tasks/Exchange/Collection ───
        composable(Screen.WithdrawRecord.route) {
            WithdrawRecordScreen(navController = navController)
        }

        // ─── Phase 5: Profile, Settings, Activities, WebView ───
        dialog(
            route = Screen.SpinWheel.route,
            dialogProperties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false)
        ) {
            SpinWheelScreen(navController = navController)
        }

        composable(
            route = Screen.WebGame.route,
            arguments = listOf(
                navArgument("kind") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            WebViewScreen(
                navController = navController,
                kind = backStackEntry.arguments?.getString("kind") ?: ""
            )
        }

        composable(Screen.Mine.route) {
            MineScreen(navController = navController)
        }

        composable(Screen.Settings.route) {
            SettingsScreen(navController = navController)
        }

        composable(Screen.RecentActivities.route) {
            RecentActivitiesScreen(navController = navController)
        }
    }
}
