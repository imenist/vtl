package com.vitalo.markrun.ui.running

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vitalo.markrun.R
import com.vitalo.markrun.ui.theme.DarkBackground
import com.vitalo.markrun.ui.theme.TextPrimary

@Composable
fun RunningBottomPanel(
    state: RunningState,
    duration: Double,
    distance: Double,
    pace: Double,
    kcal: Double,
    onGo: () -> Unit,
    onPause: () -> Unit
) {
    val outerGradient = Brush.horizontalGradient(
        colors = listOf(Color(0xFFFCF28C), Color(0xFFFCFFF7))
    )
    val innerGradient = Brush.verticalGradient(
        colors = listOf(Color(0xFFFFFFFF), Color(0xFFFFFFE9))
    )
    val pauseButtonGradient = Brush.horizontalGradient(
        colors = listOf(Color(0xFFDCD29D), Color(0xFFD6D6D6))
    )

    Surface(
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        color = Color.Transparent,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(outerGradient)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(start = 20.dp, top = 15.dp, end = 20.dp)
                ) {
                    if (state == RunningState.NOT_STARTED) {
                        Text(
                            text = "Welcome",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            fontStyle = FontStyle.Italic,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                    Text(
                        text = "Just a few more steps to unlock a surprise!",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }

                Surface(
                    shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
                    color = Color.Transparent,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(innerGradient)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Spacer(modifier = Modifier.height(36.dp))

                            RunningMetricView(
                                value = formatDuration(duration),
                                unit = "MIN",
                                valueFontSize = 40f,
                                unitFontSize = 16f
                            )

                            Spacer(modifier = Modifier.height(20.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                Box(modifier = Modifier.width(81.dp)) {
                                    RunningMetricView(
                                        value = String.format("%.2f", distance / 1000),
                                        unit = "KM"
                                    )
                                }
                                Box(modifier = Modifier.width(81.dp)) {
                                    RunningMetricView(
                                        value = String.format("%.2f", kcal),
                                        unit = "KCAL"
                                    )
                                }
                                Box(modifier = Modifier.width(81.dp)) {
                                    RunningMetricView(
                                        value = formatPace(pace),
                                        unit = "PACE"
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(29.dp))

                            if (state == RunningState.NOT_STARTED || state == RunningState.PAUSED) {
                                Button(
                                    onClick = onGo,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(64.dp)
                                        .padding(horizontal = 38.dp),
                                    shape = RoundedCornerShape(50.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = DarkBackground)
                                ) {
                                    Text(
                                        text = "GO",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Black,
                                        fontStyle = FontStyle.Italic,
                                        color = Color.White
                                    )
                                }
                            } else if (state == RunningState.RUNNING) {
                                Button(
                                    onClick = onPause,
                                    modifier = Modifier
                                        .width(132.dp)
                                        .height(64.dp),
                                    shape = RoundedCornerShape(50.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFFDCD29D)
                                    )
                                ) {
                                    Image(
                                        painter = painterResource(id = R.drawable.ic_running_pause),
                                        contentDescription = "Pause",
                                        modifier = Modifier.size(16.dp, 17.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(32.dp))
                        }
                    }
                }
            }
        }
    }
}

private fun formatDuration(seconds: Double): String {
    val s = seconds.toInt()
    val h = s / 3600
    val m = (s % 3600) / 60
    val sec = s % 60
    return String.format("%02d:%02d:%02d", h, m, sec)
}

private fun formatPace(pace: Double): String {
    if (pace <= 0) return "--"
    val min = pace.toInt()
    val sec = ((pace - min) * 60).toInt()
    return String.format("%02d:%02d", min, sec)
}
