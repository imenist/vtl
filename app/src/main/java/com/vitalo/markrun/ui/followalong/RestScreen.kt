package com.vitalo.markrun.ui.followalong

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vitalo.markrun.ui.theme.DarkBackground
import com.vitalo.markrun.ui.theme.GradientGreenEnd
import com.vitalo.markrun.ui.theme.GradientGreenStart
import kotlinx.coroutines.delay

@Composable
fun RestScreen(
    initialTime: Int = 15,
    onFinish: () -> Unit,
    onSkip: () -> Unit
) {
    var remainingTime by remember { mutableIntStateOf(initialTime) }
    var totalTime by remember { mutableIntStateOf(initialTime) }
    var canAddTime by remember { mutableStateOf(true) }

    val progress by animateFloatAsState(
        targetValue = if (totalTime > 0) remainingTime.toFloat() / totalTime.toFloat() else 0f,
        animationSpec = tween(durationMillis = 500),
        label = "rest_progress"
    )

    LaunchedEffect(remainingTime) {
        if (remainingTime > 0) {
            delay(1000)
            remainingTime -= 1
        } else {
            onFinish()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.7f)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(1f))

            // Circular progress with countdown
            Box(contentAlignment = Alignment.Center) {
                CircularProgressIndicator(
                    progress = { 1f },
                    modifier = Modifier.size(188.dp),
                    color = Color.White.copy(alpha = 0.3f),
                    strokeWidth = 12.dp,
                    strokeCap = StrokeCap.Round
                )

                CircularProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.size(188.dp),
                    color = GradientGreenStart,
                    strokeWidth = 12.dp,
                    strokeCap = StrokeCap.Round
                )

                Text(
                    text = if (remainingTime > 0) "$remainingTime" else "GO",
                    fontSize = 64.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontStyle = FontStyle.Italic,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(80.dp))

            // Skip and Add time buttons
            Row(
                horizontalArrangement = Arrangement.spacedBy(36.dp)
            ) {
                // Skip
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .width(100.dp)
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) { onSkip() }
                ) {
                    Box(
                        modifier = Modifier
                            .size(70.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("⏭", fontSize = 24.sp)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "SKIP",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }

                // Add 15s
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .width(100.dp)
                        .clickable(
                            enabled = canAddTime,
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) {
                            remainingTime += 15
                            totalTime += 15
                            canAddTime = false
                        }
                ) {
                    Box(
                        modifier = Modifier
                            .size(70.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = if (canAddTime) 0.2f else 0.06f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("+", fontSize = 28.sp, color = Color.White.copy(alpha = if (canAddTime) 1f else 0.3f))
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "+ 15s",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White.copy(alpha = if (canAddTime) 1f else 0.3f)
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))
        }
    }
}
