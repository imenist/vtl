package com.vitalo.markrun.ui.lessondetail

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.vitalo.markrun.data.remote.model.MountAction
import com.vitalo.markrun.navigation.Screen
import com.vitalo.markrun.ui.common.CommonEmptyView
import com.vitalo.markrun.ui.common.CommonErrorView
import com.vitalo.markrun.ui.theme.DarkBackground
import com.vitalo.markrun.ui.theme.GradientGreenEnd
import com.vitalo.markrun.ui.theme.GradientGreenStart

@Composable
fun LessonDetailScreen(
    navController: NavController,
    trainingCode: String,
    partIndex: Int,
    fromToday: Boolean,
    viewModel: LessonDetailViewModel = hiltViewModel()
) {
    val lesson by viewModel.lesson.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val isError by viewModel.isError.collectAsState()

    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val statusBarHeight = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val navBarHeight = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    LaunchedEffect(trainingCode) {
        if (lesson == null && !isLoading) {
            viewModel.load(trainingCode)
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(Color.White)) {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            // Cover image
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(screenWidth)
                        .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
                ) {
                    AsyncImage(
                        model = lesson?.overviewCover ?: lesson?.cover,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    // Bottom gradient overlay with info
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .fillMaxWidth()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.6f))
                                )
                            )
                            .padding(horizontal = 18.dp, vertical = 14.dp)
                    ) {
                        Text(
                            text = "Part $partIndex: ${lesson?.name ?: trainingCode}",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                    }
                }
            }

            // Action list
            item { Spacer(modifier = Modifier.height(20.dp)) }

            when {
                isLoading -> {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row {
                                CircularProgressIndicator(modifier = Modifier.size(24.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Loading...", color = Color.Gray)
                            }
                        }
                    }
                }
                isError -> {
                    item {
                        CommonErrorView(onRetry = { viewModel.reload() })
                    }
                }
                else -> {
                    val actions = lesson?.mountActions
                    if (!actions.isNullOrEmpty()) {
                        itemsIndexed(actions) { _, mountAction ->
                            ActionCardView(
                                mountAction = mountAction,
                                onClick = {
                                    mountAction.action?.let { action ->
                                        navController.navigate(
                                            "action_detail/${action.code}"
                                        )
                                    }
                                }
                            )
                        }
                    } else if (lesson != null) {
                        item { CommonEmptyView() }
                    }
                }
            }

            // Bottom spacing for button
            item { Spacer(modifier = Modifier.height(100.dp + navBarHeight)) }
        }

        // Back button
        Box(
            modifier = Modifier
                .padding(start = 20.dp, top = 8.dp + statusBarHeight)
                .size(50.dp)
                .clip(RoundedCornerShape(25.dp))
                .background(Color.Black.copy(alpha = 0.3f))
                .clickable { navController.popBackStack() },
            contentAlignment = Alignment.Center
        ) {
            Text("←", color = Color.White, fontSize = 24.sp)
        }

        // START button at bottom
        val actions = lesson?.mountActions
        if (!actions.isNullOrEmpty()) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 37.dp, vertical = 14.dp + navBarHeight)
                    .fillMaxWidth()
                    .height(69.dp)
                    .clip(RoundedCornerShape(50.dp))
                    .background(DarkBackground)
                    .clickable {
                        navController.navigate(
                            "follow_along/$trainingCode/${lesson?.code ?: ""}"
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "START",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        fontStyle = FontStyle.Italic,
                        color = Color.White
                    )
                    Text(
                        text = "Claim Reward Now",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        fontStyle = FontStyle.Italic,
                        color = GradientGreenStart
                    )
                }
            }
        }
    }
}

@Composable
fun ActionCardView(
    mountAction: MountAction,
    onClick: () -> Unit
) {
    val action = mountAction.action ?: return
    val duration = mountAction.duration ?: 0

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 20.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Thumbnail
        AsyncImage(
            model = action.thumbnail ?: action.videos?.firstOrNull()?.firstFrame,
            contentDescription = action.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(12.dp))
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = action.name ?: "",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${duration}s",
                fontSize = 12.sp,
                color = Color.Gray
            )
        }
    }
}
