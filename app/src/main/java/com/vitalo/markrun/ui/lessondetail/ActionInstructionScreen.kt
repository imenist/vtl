package com.vitalo.markrun.ui.lessondetail

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import coil.compose.AsyncImage
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.vitalo.markrun.R
import com.vitalo.markrun.ui.common.CommonLoadingView
import com.vitalo.markrun.ui.common.CommonErrorView
import com.vitalo.markrun.common.cache.VideoCacheManager
import com.vitalo.markrun.data.LessonDataStore
import kotlinx.coroutines.launch

@Composable
fun ActionInstructionScreen(
    navController: NavController,
    actionCode: String
) {
    val context = LocalContext.current
    val statusBarHeight = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val scope = rememberCoroutineScope()

    val action = LessonDataStore.currentActions
        .mapNotNull { it.action }
        .firstOrNull { it.code == actionCode }

    if (action == null) {
        CommonLoadingView()
        return
    }

    val videoUrl = action.videos?.firstOrNull()?.videoUrl
    val previewImageUrl = action.videos?.firstOrNull()?.firstFrame ?: action.thumbnail

    var isVideoReady by remember { mutableStateOf(false) }
    var downloadFailed by remember { mutableStateOf(false) }
    var isDownloading by remember { mutableStateOf(false) }
    var retryTrigger by remember { mutableStateOf(0) }

    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            repeatMode = Player.REPEAT_MODE_ALL
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
        }
    }

    fun prepareAndPlayVideo(urlStr: String) {
        val localFile = VideoCacheManager.getLocalFile(context, urlStr)
        val uri = if (localFile.exists() && localFile.length() > 0) {
            Uri.fromFile(localFile)
        } else {
            Uri.parse(urlStr)
        }
        exoPlayer.setMediaItem(MediaItem.fromUri(uri))
        exoPlayer.prepare()
        exoPlayer.playWhenReady = true
        isVideoReady = true
    }

    LaunchedEffect(videoUrl, retryTrigger) {
        if (!videoUrl.isNullOrEmpty()) {
            val localFile = VideoCacheManager.getLocalFile(context, videoUrl)
            if (localFile.exists() && localFile.length() > 0) {
                prepareAndPlayVideo(videoUrl)
            } else {
                isDownloading = true
                downloadFailed = false
                val downloadedFile = VideoCacheManager.downloadVideo(context, videoUrl) { _ -> }
                isDownloading = false
                if (downloadedFile != null && downloadedFile.exists()) {
                    prepareAndPlayVideo(videoUrl)
                } else {
                    downloadFailed = true
                }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(Color.White)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Dark header area with video player
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
                    .background(Color(0xFF262625))
            ) {
                Box(
                    modifier = Modifier
                        .padding(horizontal = 20.dp, vertical = 20.dp)
                        .padding(top = 50.dp + statusBarHeight)
                        .fillMaxSize()
                        .clip(RoundedCornerShape(26.dp))
                        .background(Color.Gray)
                ) {
                    if (isVideoReady) {
                        AndroidView(
                            factory = { ctx ->
                                PlayerView(ctx).apply {
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
                    } else if (downloadFailed) {
                        CommonErrorView(onRetry = {
                            retryTrigger++
                        })
                    } else if (previewImageUrl != null) {
                        AsyncImage(
                            model = previewImageUrl,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }

                // Top bar
                Box(
                    modifier = Modifier
                        .padding(start = 20.dp, top = 8.dp + statusBarHeight)
                        .size(50.dp)
                        .clickable { navController.popBackStack() },
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_back),
                        contentDescription = "Back",
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Text(
                    text = "Instruction",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 20.dp + statusBarHeight)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Action name
            Text(
                text = action.name ?: "",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black,
                modifier = Modifier.padding(horizontal = 20.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Steps
            val steps = action.steps ?: ""
            if (steps.isNotEmpty()) {
                val stepLines = steps.split("\n", "\r\n")
                    .filter { it.isNotBlank() }

                Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                    stepLines.forEach { step ->
                        Text(
                            text = step.trim(),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.Black.copy(alpha = 0.5f),
                            lineHeight = 20.sp,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}