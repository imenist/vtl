package com.vitalo.markrun.ui.running

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.android.gms.maps.model.LatLng
import com.vitalo.markrun.navigation.Screen

@Composable
fun RunningScreen(
    navController: NavController,
    viewModel: RunningViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val locations by viewModel.locations.collectAsState()
    val duration by viewModel.duration.collectAsState()
    val distance by viewModel.distance.collectAsState()

    var showCountDown by remember { mutableStateOf(false) }
    var showPauseOverlay by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var isFollowingUser by remember { mutableStateOf(true) }

    val mapLocations = remember(locations) {
        locations.map { LatLng(it.latitude, it.longitude) }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize().statusBarsPadding()) {
            RunningTitleBar(
                title = "Start Tracking",
                onBack = {
                    when (state) {
                        RunningState.RUNNING -> {
                            viewModel.pauseRunning()
                            showPauseOverlay = true
                        }
                        else -> {
                            navController.popBackStack()
                        }
                    }
                }
            )

            Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                // Map layer
                Box(modifier = Modifier.fillMaxSize()) {
                    RunningMapView(
                        locations = mapLocations,
                        modifier = Modifier.fillMaxSize(),
                        isFollowingUser = isFollowingUser
                    )

                    // Top gradient overlay
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(103.dp)
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.White,
                                        Color.White.copy(alpha = 0f)
                                    )
                                )
                            )
                    )

                    // Bottom gradient overlay
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(94.dp)
                            .align(Alignment.BottomCenter)
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Black.copy(alpha = 0f),
                                        Color.Black.copy(alpha = 0.5f)
                                    )
                                )
                            )
                    )
                }

                // Bottom panel
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Bottom
                ) {
                    RunningBottomPanel(
                        state = state,
                        duration = duration,
                        distance = distance,
                        pace = viewModel.activePace,
                        kcal = viewModel.activeKcal,
                        onGo = {
                            showCountDown = true
                        },
                        onPause = {
                            viewModel.pauseRunning()
                            showPauseOverlay = true
                        }
                    )
                }
            }
        }

        // Countdown overlay
        if (showCountDown) {
            StartCountDownOverlay {
                showCountDown = false
                if (state == RunningState.NOT_STARTED) {
                    viewModel.startRunning()
                } else {
                    viewModel.resumeRunning()
                }
            }
        }

        // Pause overlay
        if (showPauseOverlay) {
            RunningPauseOverlay(
                onStop = {
                    isLoading = true
                    showPauseOverlay = false
                    viewModel.stopRunning { recordId ->
                        isLoading = false
                        if (recordId != null) {
                            navController.navigate(
                                Screen.RunningResult.route
                                    .replace("{recordId}", recordId.toString())
                                    .replace("{fragmentsCount}", "0")
                            ) {
                                popUpTo(Screen.RunTracker.route) { inclusive = true }
                            }
                        }
                    }
                },
                onContinue = {
                    showPauseOverlay = false
                    isFollowingUser = true
                    viewModel.resumeRunning()
                }
            )
        }

        // Loading overlay
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = Color.White)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Saving...", color = Color.White)
                }
            }
        }
    }
}
