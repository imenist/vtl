package com.vitalo.markrun.ui.followalong

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.navigation.NavController
import com.vitalo.markrun.navigation.Screen
import com.vitalo.markrun.R
import com.vitalo.markrun.data.remote.model.Lesson
import com.vitalo.markrun.data.remote.model.MountAction
import com.vitalo.markrun.data.remote.model.Training
import com.vitalo.markrun.ui.theme.DarkBackground
import com.vitalo.markrun.ui.theme.GradientGreenEnd
import com.vitalo.markrun.ui.theme.GradientGreenStart
import kotlinx.coroutines.delay
import kotlin.math.max

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
                Screen.WorkoutResult.createRoute(
                    duration = viewModel.totalWorkoutMinutes,
                    calorie = viewModel.totalWorkoutCalorie,
                    hasCompleted = true
                )
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
            val localFile = com.vitalo.markrun.common.cache.VideoCacheManager.getLocalFile(context, videoUrl)
            val uri = if (localFile.exists() && localFile.length() > 0) {
                Uri.fromFile(localFile)
            } else {
                Uri.parse(videoUrl)
            }
            exoPlayer.setMediaItem(MediaItem.fromUri(uri))
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

    val screenWidth = androidx.compose.ui.platform.LocalConfiguration.current.screenWidthDp.dp

    Box(modifier = Modifier.fillMaxSize().background(Color.White)) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Video player area (Square 1:1, zoomed to fill)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(screenWidth)
                    .padding(top = statusBarHeight + 50.dp)
            ) {
                // Loading Background
                Box(
                    modifier = Modifier.fillMaxSize().background(Color.Gray),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Loading...", color = Color.White)
                }

                AndroidView(
                    factory = { ctx ->
                        androidx.media3.ui.PlayerView(ctx).apply {
                            player = exoPlayer
                            useController = false
                            resizeMode = androidx.media3.ui.AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                            layoutParams = android.view.ViewGroup.LayoutParams(
                                android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                                android.view.ViewGroup.LayoutParams.MATCH_PARENT
                            )
                        }
                    },
                    update = { view ->
                        if (view.player != exoPlayer) {
                            view.player = exoPlayer
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )

                // Top controls
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .align(Alignment.TopStart),
                    horizontalAlignment = Alignment.End
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Close button
                        Image(
                            painter = painterResource(id = R.drawable.ic_close),
                            contentDescription = "Close",
                            modifier = Modifier
                                .size(50.dp)
                                .clickable {
                                    viewModel.setPlaying(false)
                                    showExitConfirm = true
                                }
                        )

                        Spacer(modifier = Modifier.weight(1f))

                        // Guide button
                        Image(
                            painter = painterResource(id = R.drawable.ic_video_guide),
                            contentDescription = "Guide",
                            modifier = Modifier
                                .size(50.dp)
                                .clickable {
                                    viewModel.setPlaying(false)
                                    val action = viewModel.currentAction
                                    if (action != null) {
                                        navController.navigate("action_detail/${action.code}")
                                    }
                                }
                        )

                        // Mute button
                        Image(
                            painter = painterResource(id = if (isMuted) R.drawable.ic_sound_off else R.drawable.ic_sound_on),
                            contentDescription = "Mute",
                            modifier = Modifier
                                .size(50.dp)
                                .clickable { viewModel.toggleMute() }
                        )
                    }

                    Spacer(modifier = Modifier.height(15.dp))
                    
                    val bonusProgress by viewModel.bonusProgress.collectAsState()
                    WorkoutBonusTimerView(progress = bonusProgress)
                }
            }

            Spacer(modifier = Modifier.weight(1f))
        }

        // "Keep It Up!" card
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 215.dp + navBarHeight + 20.dp)
                .fillMaxWidth()
                .height(100.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(Color(0xFFFDFFFE), Color(0xFFFDF28C))
                        )
                    )
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 14.dp)
                ) {
                    Text(
                        text = "Keep It Up!",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                        color = Color(0xFF0D120E)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "You'll get Fragments and CaloCoins",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF0D120E)
                    )
                }
            }
            
            Image(
                painter = painterResource(id = R.drawable.img_lesson_fragment_n_coin),
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(y = (-38).dp)
                    .size(width = 117.74.dp, height = 103.6.dp)
            )
        }

        // Bottom controls panel
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                .background(Color.White)
                .height(243.dp + navBarHeight + 20.dp)
                .padding(top = 30.dp, bottom = navBarHeight + 20.dp)
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
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    Text(
                        text = currentAction.name ?: "",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black,
                        modifier = Modifier.padding(top = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

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
                            .size(width = 91.dp, height = 75.dp)
                            .clickable(enabled = currentIndex > 0) {
                                viewModel.setPlaying(false)
                                showRestPage = true
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_track_previous),
                            contentDescription = "Previous",
                            modifier = Modifier.size(20.dp),
                            alpha = if (currentIndex > 0) 1f else 0.3f
                        )
                    }

                    // Play/Pause
                    Box(
                        modifier = Modifier
                            .size(width = 91.dp, height = 75.dp)
                            .clickable { viewModel.togglePlay() },
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = if (isPlaying) R.drawable.ic_track_pause else R.drawable.ic_track_play),
                            contentDescription = "Play/Pause",
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Next
                    Box(
                        modifier = Modifier
                            .size(width = 91.dp, height = 75.dp)
                            .clickable(enabled = currentIndex < viewModel.totalCount - 1) {
                                viewModel.setPlaying(false)
                                showRestPage = true
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_track_next),
                            contentDescription = "Next",
                            modifier = Modifier.size(20.dp),
                            alpha = if (currentIndex < viewModel.totalCount - 1) 1f else 0.3f
                        )
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
                    navController.navigate(
                        com.vitalo.markrun.navigation.Screen.WorkoutResult.createRoute(
                            duration = viewModel.totalWorkoutMinutes,
                            calorie = viewModel.totalWorkoutCalorie,
                            hasCompleted = false
                        )
                    ) {
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
fun WorkoutBonusTimerView(
    progress: Double,
    durationInSec: Int = 7
) {
    val secondsLeft = max(0, durationInSec - (progress * durationInSec).toInt())
    
    Box(
        modifier = Modifier.size(60.dp),
        contentAlignment = Alignment.Center
    ) {
        // Background circle
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .background(
                    Brush.linearGradient(
                        colors = listOf(Color.White, Color(0xFFDD5252).copy(alpha = 0.45f)),
                        start = Offset(0.56f, 0.87f),
                        end = Offset(0.55f, 0.31f)
                    )
                )
        )

        // Progress ring
        Canvas(modifier = Modifier.size(51.dp)) {
            drawCircle(
                color = Color.White,
                style = Stroke(width = 8.dp.toPx())
            )
            
            val sweepAngle = (progress * 360 * 0.75).toFloat()
            drawArc(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFFFD4D82),
                        Color(0xFFFF8F45),
                        Color(0xFFF7D129)
                    )
                ),
                startAngle = 135f,
                sweepAngle = sweepAngle,
                useCenter = false,
                style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round)
            )
        }

        // Coin
        Image(
            painter = painterResource(id = R.drawable.ic_workout_counter_coin),
            contentDescription = null,
            modifier = Modifier.size(28.dp)
        )

        // Timer text
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset(y = 12.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0xFFFD4D82),
                            Color(0xFFFF8F45),
                            Color(0xFFF7D129)
                        )
                    )
                )
                .padding(horizontal = 8.dp, vertical = 2.dp)
        ) {
            Text(
                text = "${secondsLeft}s",
                color = Color.White,
                fontSize = 10.sp,
                fontWeight = FontWeight.Black,
                fontStyle = FontStyle.Italic,
                textAlign = TextAlign.Center
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
