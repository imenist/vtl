package com.vitalo.markrun.ui.followalong

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.navigation.NavController
import com.vitalo.markrun.data.remote.model.Lesson
import com.vitalo.markrun.data.remote.model.MountAction
import com.vitalo.markrun.data.remote.model.Training
import com.vitalo.markrun.ui.theme.DarkBackground
import com.vitalo.markrun.ui.theme.GradientGreenEnd
import com.vitalo.markrun.ui.theme.GradientGreenStart
import kotlinx.coroutines.delay

@Composable
fun FollowAlongScreen(
    navController: NavController,
    trainingCode: String,
    lessonCode: String,
    actions: List<MountAction>,
    training: Training? = null,
    viewModel: FollowAlongViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val currentIndex by viewModel.currentIndex.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()
    val isMuted by viewModel.isMuted.collectAsState()
    val progress by viewModel.progress.collectAsState()
    val currentPlayedSeconds by viewModel.currentPlayedSeconds.collectAsState()
    val hasCompleted by viewModel.hasCompleted.collectAsState()

    val statusBarHeight = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val navBarHeight = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    var showRestPage by remember { mutableStateOf(false) }
    var showExitConfirm by remember { mutableStateOf(false) }

    // Load actions
    LaunchedEffect(Unit) {
        viewModel.load(actions, training)
    }

    // Handle completion
    LaunchedEffect(hasCompleted) {
        if (hasCompleted) {
            navController.navigate(
                "workout_result"
            ) {
                popUpTo("follow_along/$trainingCode/$lessonCode") { inclusive = true }
            }
        }
    }

    // ExoPlayer setup
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            repeatMode = Player.REPEAT_MODE_ALL
        }
    }

    // Update video when action changes
    LaunchedEffect(currentIndex) {
        val videoUrl = viewModel.currentVideoUrl
        if (videoUrl != null) {
            exoPlayer.setMediaItem(MediaItem.fromUri(Uri.parse(videoUrl)))
            exoPlayer.prepare()
            exoPlayer.playWhenReady = true
            viewModel.setPlaying(true)
        }
    }

    // Sync mute state
    LaunchedEffect(isMuted) {
        exoPlayer.volume = if (isMuted) 0f else 1f
    }

    // Sync play/pause
    LaunchedEffect(isPlaying) {
        exoPlayer.playWhenReady = isPlaying
    }

    // Timer for tracking played seconds
    LaunchedEffect(isPlaying, currentIndex) {
        while (isPlaying) {
            delay(200)
            if (viewModel.isPlaying.value) {
                viewModel.updatePlayedSeconds(viewModel.currentPlayedSeconds.value + 0.2)
            }
        }
    }

    // Set auto play complete callback
    LaunchedEffect(Unit) {
        viewModel.onAutoPlayComplete = {
            exoPlayer.pause()
            showRestPage = true
        }

        viewModel.onActionChanged = {
            // Player will be updated by the LaunchedEffect(currentIndex)
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
        }
    }

    val currentAction = viewModel.currentAction
    val actionDuration = actions.getOrNull(currentIndex)?.duration ?: 0
    val remaining = maxOf(0.0, actionDuration.toDouble() - currentPlayedSeconds)
    val minutes = (remaining.toInt()) / 60
    val seconds = (remaining.toInt()) % 60
    val timeText = String.format("%02d:%02d", minutes, seconds)

    Box(modifier = Modifier.fillMaxSize().background(Color.White)) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Video player area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(top = statusBarHeight + 50.dp)
            ) {
                AndroidView(
                    factory = { ctx ->
                        PlayerView(ctx).apply {
                            player = exoPlayer
                            useController = false
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )

                // Top controls
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .align(Alignment.TopStart)
                        .padding(top = statusBarHeight)
                ) {
                    // Close button
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clickable {
                                viewModel.setPlaying(false)
                                showExitConfirm = true
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text("✕", color = Color.White, fontSize = 20.sp)
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    // Mute button
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clickable { viewModel.toggleMute() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (isMuted) "🔇" else "🔊",
                            fontSize = 20.sp
                        )
                    }
                }
            }

            // Bottom controls panel
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                    .background(Color.White)
                    .padding(top = 20.dp, bottom = navBarHeight + 20.dp)
                    .padding(horizontal = 20.dp)
            ) {
                // Time and index
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = timeText,
                        fontSize = 40.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = DarkBackground
                    )

                    Text(
                        text = "${currentIndex + 1}/${viewModel.totalCount}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black,
                        modifier = Modifier
                            .clip(RoundedCornerShape(22.dp))
                            .background(Color(0xFFE2FF04))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }

                // Action name
                if (currentAction != null) {
                    Text(
                        text = currentAction.name ?: "",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black,
                        modifier = Modifier.padding(top = 3.dp)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Progress bar and controls
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(75.dp)
                        .clip(RoundedCornerShape(20.dp))
                ) {
                    // Background track
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0xFFF8F8F8))
                    )

                    // Progress fill
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(fraction = progress.toFloat())
                            .height(75.dp)
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(GradientGreenEnd, GradientGreenStart)
                                )
                            )
                    )

                    // Control buttons
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Previous
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clickable(enabled = currentIndex > 0) {
                                    viewModel.setPlaying(false)
                                    showRestPage = true
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "⏮",
                                fontSize = 20.sp,
                                color = if (currentIndex > 0) Color.Black else Color.Black.copy(alpha = 0.3f)
                            )
                        }

                        // Play/Pause
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clickable { viewModel.togglePlay() },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (isPlaying) "⏸" else "▶",
                                fontSize = 24.sp
                            )
                        }

                        // Next
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clickable(enabled = currentIndex < viewModel.totalCount - 1) {
                                    viewModel.setPlaying(false)
                                    showRestPage = true
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "⏭",
                                fontSize = 20.sp,
                                color = if (currentIndex < viewModel.totalCount - 1)
                                    Color.Black else Color.Black.copy(alpha = 0.3f)
                            )
                        }
                    }
                }
            }
        }

        // Rest page overlay
        if (showRestPage) {
            RestScreen(
                onFinish = {
                    showRestPage = false
                    viewModel.continueToNextAction()
                    viewModel.setPlaying(true)
                },
                onSkip = {
                    showRestPage = false
                    viewModel.continueToNextAction()
                    viewModel.setPlaying(true)
                }
            )
        }

        // Exit confirm overlay
        if (showExitConfirm) {
            WorkoutExitOverlay(
                onExit = {
                    showExitConfirm = false
                    navController.navigate("workout_result") {
                        popUpTo("follow_along/$trainingCode/$lessonCode") { inclusive = true }
                    }
                },
                onContinue = {
                    showExitConfirm = false
                    viewModel.setPlaying(true)
                }
            )
        }
    }
}

@Composable
fun WorkoutExitOverlay(
    onExit: () -> Unit,
    onContinue: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.7f)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(
                text = "Quit Workout?",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Text(
                text = "You'll lose your progress if you quit now.",
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.8f)
            )

            // Continue button
            Box(
                modifier = Modifier
                    .width(200.dp)
                    .height(56.dp)
                    .clip(RoundedCornerShape(28.dp))
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(GradientGreenEnd, GradientGreenStart)
                        )
                    )
                    .clickable { onContinue() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "CONTINUE",
                    fontWeight = FontWeight.Black,
                    color = DarkBackground
                )
            }

            // Exit button
            Text(
                text = "Exit",
                fontSize = 16.sp,
                color = Color.White.copy(alpha = 0.6f),
                modifier = Modifier
                    .clickable { onExit() }
                    .padding(8.dp)
            )
        }
    }
}
