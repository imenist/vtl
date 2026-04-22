package com.vitalo.markrun.ui.stepcounter

import android.widget.Toast
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.vitalo.markrun.R
import com.vitalo.markrun.data.remote.model.StepMilestoneStatus

@Composable
fun StepCounterView(
    viewModel: StepCounterViewModel = hiltViewModel()
) {
    val currentSteps by viewModel.currentSteps.collectAsState()
    val todayConverted by viewModel.todayConvertedSteps.collectAsState()
    val cumulativeSteps by viewModel.cumulativeSteps.collectAsState()
    val milestones by viewModel.milestones.collectAsState()
    val context = LocalContext.current

    val animatedProgress by animateFloatAsState(
        targetValue = viewModel.progress,
        animationSpec = tween(1000),
        label = "progress"
    )

    LaunchedEffect(Unit) {
        viewModel.startUpdates()
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier.size(259.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            Image(
                painter = painterResource(id = R.drawable.img_step_progress),
                contentDescription = null,
                modifier = Modifier
                    .size(259.dp)
                    .padding(top = 21.dp),
                contentScale = ContentScale.Fit
            )

            val arcColor1 = Color(0xFFAFFF24)
            val arcColor2 = Color(0xFFFFEB09)
            Box(
                modifier = Modifier
                    .padding(top = 46.dp)
                    .size(209.dp)
                    .rotate(-50.5f)
                    .drawBehind {
                        val strokeWidth = 8.dp.toPx()
                        val fullSweep = 280.8f
                        drawArc(
                            brush = Brush.sweepGradient(listOf(arcColor1, arcColor2)),
                            startAngle = -90f,
                            sweepAngle = fullSweep * animatedProgress,
                            useCenter = false,
                            style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                            topLeft = Offset(strokeWidth / 2, strokeWidth / 2),
                            size = Size(size.width - strokeWidth, size.height - strokeWidth)
                        )
                    }
            )

            Image(
                painter = painterResource(id = R.drawable.img_step_figure_female),
                contentDescription = null,
                modifier = Modifier
                    .width(84.dp)
                    .height(129.dp),
                contentScale = ContentScale.Fit
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 126.dp)
            ) {
                Text(
                    text = "$currentSteps",
                    fontSize = 52.sp,
                    fontWeight = FontWeight.Black,
                    fontStyle = FontStyle.Italic,
                    color = Color(0xFF0D120E)
                )
                Text(
                    text = "Limit: ${viewModel.dailyGoal}",
                    fontSize = 12.sp,
                    color = Color(0xFF0D120E).copy(alpha = 0.6f)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        val coinsCanEarn = viewModel.getCoinsCanEarn()
        Box(
            modifier = Modifier
                .width(267.dp)
                .height(56.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(28.dp))
                    .background(Color(0xFF0D120E))
                    .clickable {
                        val earned = viewModel.convertSteps()
                        if (earned > 0) {
                            Toast
                                .makeText(context, "+$earned CaloCoins!", Toast.LENGTH_SHORT)
                                .show()
                        } else {
                            Toast
                                .makeText(context, "No convertible steps yet", Toast.LENGTH_SHORT)
                                .show()
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Convert to CaloCoin",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 9.dp, y = (-9).dp)
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomEnd = 16.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(Color(0xFFC9FF6B), Color(0xFFFFED29))
                        )
                    )
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "$coinsCanEarn",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0D120E)
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Today's conversion: ${minOf(todayConverted, viewModel.dailyGoal)}/${viewModel.dailyGoal}",
            fontSize = 12.sp,
            color = Color.Black.copy(alpha = 0.4f)
        )

        Spacer(modifier = Modifier.height(20.dp))

        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Color.White.copy(alpha = 0.9f))
                .padding(vertical = 12.dp),
            contentPadding = PaddingValues(horizontal = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            itemsIndexed(milestones) { index, milestone ->
                val effectiveStatus = viewModel.getEffectiveStatus(index)
                MilestoneItem(
                    rewardCoins = milestone.rewardCoins,
                    requiredSteps = milestone.requiredSteps,
                    status = effectiveStatus,
                    progress = if (milestone.requiredSteps > 0)
                        minOf(cumulativeSteps.toFloat() / milestone.requiredSteps, 1f) else 1f,
                    onClick = {
                        when (effectiveStatus) {
                            StepMilestoneStatus.CLAIMABLE -> viewModel.claimMilestone(index)
                            StepMilestoneStatus.CLAIMED -> {
                                Toast.makeText(context, "Already claimed", Toast.LENGTH_SHORT).show()
                            }
                            StepMilestoneStatus.LOCKED -> {
                                Toast.makeText(context, "Keep walking to unlock", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                )
            }
        }
    }
}

@Composable
@Suppress("UNUSED_PARAMETER")
fun MilestoneItem(
    rewardCoins: Int,
    requiredSteps: Int,
    status: StepMilestoneStatus,
    progress: Float,
    onClick: () -> Unit
) {
    val bgBrush = when (status) {
        StepMilestoneStatus.CLAIMED -> Brush.linearGradient(
            listOf(Color(0x33D6D6D6), Color(0x33DBD19E))
        )
        StepMilestoneStatus.CLAIMABLE -> Brush.linearGradient(
            listOf(Color(0xFFFFFFFF), Color(0xFFFFF2D1))
        )
        StepMilestoneStatus.LOCKED -> Brush.linearGradient(
            listOf(Color(0xFFC9E8FF), Color(0xFFE3FC8C))
        )
    }

    Column(
        modifier = Modifier
            .width(62.dp)
            .height(92.dp)
            .clip(RoundedCornerShape(12.5.dp))
            .background(bgBrush)
            .clickable { onClick() }
            .padding(vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = "🪙", fontSize = 24.sp)

        Text(
            text = "+$rewardCoins",
            fontSize = 13.sp,
            fontWeight = FontWeight.Black,
            fontStyle = FontStyle.Italic,
            color = when (status) {
                StepMilestoneStatus.CLAIMED -> Color(0xFFB5B08D)
                else -> Color(0xFFFF9814)
            }
        )

        Box(
            modifier = Modifier
                .width(51.dp)
                .height(20.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(
                    when (status) {
                        StepMilestoneStatus.CLAIMED -> Brush.horizontalGradient(
                            listOf(Color(0xFFD6D6D6), Color(0xFFDBD19E))
                        )
                        StepMilestoneStatus.CLAIMABLE -> Brush.horizontalGradient(
                            listOf(Color(0xFFFD4D82), Color(0xFFFF8F45))
                        )
                        StepMilestoneStatus.LOCKED -> Brush.horizontalGradient(
                            listOf(Color(0xFFDBD19E), Color(0xFFD6D6D6))
                        )
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            when (status) {
                StepMilestoneStatus.CLAIMED -> Text("✓", fontSize = 10.sp, color = Color.White)
                StepMilestoneStatus.CLAIMABLE -> Text(
                    "Claim", fontSize = 9.sp,
                    fontWeight = FontWeight.Black, fontStyle = FontStyle.Italic,
                    color = Color.White
                )
                StepMilestoneStatus.LOCKED -> Text(
                    "$requiredSteps", fontSize = 10.sp,
                    fontWeight = FontWeight.Bold, color = Color.White
                )
            }
        }
    }
}
