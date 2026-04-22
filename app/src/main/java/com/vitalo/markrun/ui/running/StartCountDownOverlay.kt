package com.vitalo.markrun.ui.running

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vitalo.markrun.ui.theme.GradientGreenEnd
import com.vitalo.markrun.ui.theme.GradientGreenStart
import kotlinx.coroutines.delay

@Composable
fun StartCountDownOverlay(onStart: () -> Unit) {
    var remainingTime by remember { mutableIntStateOf(3) }
    val targetProgress = remainingTime / 3f
    val animatedProgress by animateFloatAsState(
        targetValue = targetProgress,
        animationSpec = tween(durationMillis = 500),
        label = "countdown_progress"
    )

    LaunchedEffect(Unit) {
        while (remainingTime > 0) {
            delay(1000)
            remainingTime--
        }
        delay(1000)
        onStart()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.7f)),
        contentAlignment = Alignment.Center
    ) {
        Box(contentAlignment = Alignment.Center) {
            Canvas(modifier = Modifier.size(188.dp)) {
                val strokeWidth = 12.dp.toPx()
                val padding = strokeWidth / 2
                val arcSize = Size(size.width - strokeWidth, size.height - strokeWidth)

                drawCircle(
                    color = Color.White.copy(alpha = 0.3f),
                    radius = (size.width - strokeWidth) / 2,
                    style = Stroke(width = strokeWidth)
                )

                drawArc(
                    brush = Brush.verticalGradient(
                        colors = listOf(GradientGreenStart, GradientGreenEnd)
                    ),
                    startAngle = -90f,
                    sweepAngle = 360f * animatedProgress,
                    useCenter = false,
                    topLeft = Offset(padding, padding),
                    size = arcSize,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )
            }

            Text(
                text = if (remainingTime > 0) "$remainingTime" else "GO",
                fontSize = 64.sp,
                fontWeight = FontWeight.SemiBold,
                fontStyle = FontStyle.Italic,
                color = Color.White
            )
        }
    }
}
