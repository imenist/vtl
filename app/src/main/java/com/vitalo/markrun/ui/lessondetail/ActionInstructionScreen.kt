package com.vitalo.markrun.ui.lessondetail

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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.vitalo.markrun.ui.common.CommonErrorView
import com.vitalo.markrun.ui.common.CommonLoadingView

@Composable
fun ActionInstructionScreen(
    navController: NavController,
    actionCode: String,
    viewModel: LessonDetailViewModel = hiltViewModel()
) {
    val lesson by viewModel.lesson.collectAsState()
    val statusBarHeight = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()

    val action = lesson?.mountActions
        ?.mapNotNull { it.action }
        ?.firstOrNull { it.code == actionCode }

    if (action == null) {
        CommonLoadingView()
        return
    }

    Box(modifier = Modifier.fillMaxSize().background(Color.White)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Dark header area with video thumbnail
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
                    .background(Color(0xFF262625))
            ) {
                AsyncImage(
                    model = action.videos?.firstOrNull()?.firstFrame ?: action.thumbnail,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .padding(20.dp)
                        .fillMaxSize()
                        .clip(RoundedCornerShape(26.dp))
                )

                // Top bar
                Box(
                    modifier = Modifier
                        .padding(start = 20.dp, top = 8.dp + statusBarHeight)
                        .size(50.dp)
                        .clip(RoundedCornerShape(25.dp))
                        .clickable { navController.popBackStack() },
                    contentAlignment = Alignment.Center
                ) {
                    Text("←", color = Color.White, fontSize = 24.sp)
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
