package com.vitalo.markrun.ui.splash

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.vitalo.markrun.navigation.Screen
import com.vitalo.markrun.ui.common.CommonLottieView
import com.vitalo.markrun.ui.theme.GradientGreenStart
import com.vitalo.markrun.ui.theme.ProgressBarTrack

@Composable
fun SplashScreen(
    navController: NavController,
    viewModel: SplashViewModel = hiltViewModel()
) {
    val progress by viewModel.progress.collectAsState()
    val shouldNavigate by viewModel.shouldNavigate.collectAsState()

    LaunchedEffect(shouldNavigate) {
        when (shouldNavigate) {
            SplashNavTarget.HOME -> navController.navigate(Screen.Home.route) {
                popUpTo(Screen.Splash.route) { inclusive = true }
            }
            SplashNavTarget.ONBOARDING -> navController.navigate(Screen.BeginnerGuide.route) {
                popUpTo(Screen.Splash.route) { inclusive = true }
            }
            else -> {}
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        CommonLottieView(
            animationName = "SplashBackground",
            loop = false,
            modifier = Modifier.fillMaxSize()
        )

        CommonLottieView(
            animationName = "SplashForeground",
            loop = false,
            modifier = Modifier.fillMaxSize()
        )

        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 66.dp, vertical = 48.dp)
                .fillMaxWidth(),
            color = GradientGreenStart,
            trackColor = ProgressBarTrack,
        )
    }
}
