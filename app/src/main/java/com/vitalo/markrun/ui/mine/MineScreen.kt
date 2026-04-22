package com.vitalo.markrun.ui.mine

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.vitalo.markrun.R
import com.vitalo.markrun.navigation.Screen

@Composable
fun MineScreen(
    navController: NavController,
    viewModel: MineViewModel = hiltViewModel(),
    weightChartViewModel: WeightChartViewModel = hiltViewModel()
) {
    val totalDistance by viewModel.totalDistance.collectAsState()
    val totalDuration by viewModel.totalDuration.collectAsState()
    val bestPace by viewModel.bestPace.collectAsState()
    val totalCalories by viewModel.totalCalories.collectAsState()
    val longestDistance by viewModel.longestDistance.collectAsState()
    val longestDuration by viewModel.longestDuration.collectAsState()
    val bestPaceRecord by viewModel.bestPaceRecord.collectAsState()
    val showAddWeightDialog by weightChartViewModel.showAddDialog.collectAsState()

    val blurRadius = if (showAddWeightDialog) 5.dp else 0.dp

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .blur(blurRadius)
        ) {
            MineHeaderView(
                onTapBack = { navController.popBackStack() },
                onTapSetting = { navController.navigate(Screen.Settings.route) }
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp)
            ) {
                Spacer(modifier = Modifier.height(23.dp))

                // My Progress section
                SectionHeader(
                    title = "My Progress",
                    actionText = "MORE",
                    onAction = { navController.navigate(Screen.RecentActivities.route) }
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Statistics card
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFFF4F3EE).copy(alpha = 0.4f))
                        .padding(horizontal = 20.dp, vertical = 16.dp)
                ) {
                    Text(
                        text = totalDistance,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF0D120E)
                    )
                    Text(
                        text = "TOTAL(km)",
                        fontSize = 12.sp,
                        color = Color(0xFF0D120E)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = totalDuration,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF0D120E)
                            )
                            Text(text = "MIN", fontSize = 12.sp, color = Color(0xFF0D120E))
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = bestPace,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF0D120E)
                            )
                            Text(text = "PACE (min/km)", fontSize = 12.sp, color = Color(0xFF0D120E))
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = totalCalories,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF0D120E)
                            )
                            Text(text = "KCAL", fontSize = 12.sp, color = Color(0xFF0D120E))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(23.dp))

                // Best Record section
                SectionHeader(title = "Best Record")

                Spacer(modifier = Modifier.height(10.dp))

                BestRecordItem(
                    iconRes = R.drawable.ic_record_distance,
                    title = "Longest Distance",
                    value = longestDistance,
                    unit = "km"
                )
                Spacer(modifier = Modifier.height(8.dp))
                BestRecordItem(
                    iconRes = R.drawable.ic_record_pace,
                    title = "Best Pace",
                    value = bestPaceRecord,
                    unit = "min/km"
                )
                Spacer(modifier = Modifier.height(8.dp))
                BestRecordItem(
                    iconRes = R.drawable.ic_record_duration,
                    title = "Longest Duration",
                    value = longestDuration,
                    unit = ""
                )

                Spacer(modifier = Modifier.height(23.dp))

                // Weight section
                SectionHeader(
                    title = "Weight",
                    actionText = "ADD",
                    onAction = { weightChartViewModel.showAddDialog() }
                )

                Spacer(modifier = Modifier.height(10.dp))

                WeightChartSection(viewModel = weightChartViewModel)

                Spacer(modifier = Modifier.height(32.dp))
            }
        }

        if (showAddWeightDialog) {
            AddWeightDialog(
                onSave = { record ->
                    if (record != null) {
                        weightChartViewModel.addRecord(record)
                    }
                    weightChartViewModel.hideAddDialog()
                }
            )
        }
    }
}

@Composable
private fun MineHeaderView(
    onTapBack: () -> Unit,
    onTapSetting: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF0D120E))
            .statusBarsPadding()
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clickable { onTapBack() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "←",
                    fontSize = 22.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }

            Text(
                text = "Mine",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clickable { onTapSetting() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "⚙",
                    fontSize = 20.sp,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun SectionHeader(
    title: String,
    actionText: String? = null,
    onAction: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0D120E)
        )

        if (actionText != null && onAction != null) {
            Text(
                text = actionText,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black,
                modifier = Modifier
                    .clip(RoundedCornerShape(22.dp))
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(Color(0xFFFFED29), Color(0xFFC9FF6B))
                        )
                    )
                    .clickable { onAction() }
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            )
        }
    }
}

@Composable
private fun BestRecordItem(
    iconRes: Int,
    title: String,
    value: String,
    unit: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 42.dp, bottomStart = 42.dp, topEnd = 20.dp, bottomEnd = 20.dp))
            .background(Color(0xFFF4F3EE).copy(alpha = 0.4f))
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = iconRes),
            contentDescription = title,
            modifier = Modifier.size(64.dp),
            contentScale = ContentScale.Fit
        )
        Spacer(modifier = Modifier.width(20.dp))
        Column {
            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = value,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )
                if (unit.isNotEmpty()) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = unit,
                        fontSize = 12.sp,
                        color = Color(0xFF0D120E)
                    )
                }
            }
        }
    }
}
