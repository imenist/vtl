package com.vitalo.markrun.ui.recentactivities

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.vitalo.markrun.data.local.db.entity.RunningRecord
import com.vitalo.markrun.navigation.Screen
import com.vitalo.markrun.ui.common.CommonEmptyView
import com.vitalo.markrun.ui.common.CommonLoadingView

@Composable
fun RecentActivitiesScreen(
    navController: NavController,
    viewModel: RecentActivitiesViewModel = hiltViewModel()
) {
    val records by viewModel.records.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Header
        RecentActivitiesHeader(onTapBack = { navController.popBackStack() })

        when {
            isLoading -> {
                CommonLoadingView(modifier = Modifier.weight(1f))
            }
            records.isEmpty() -> {
                CommonEmptyView(modifier = Modifier.weight(1f))
            }
            else -> {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(records, key = { it.id }) { record ->
                        RecentActivityItem(
                            record = record,
                            onClick = {
                                navController.navigate("running_detail/${record.id}")
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RecentActivitiesHeader(onTapBack: () -> Unit) {
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
                text = "Recent Activities",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )

            Spacer(modifier = Modifier.size(40.dp))
        }
    }
}

@Composable
private fun RecentActivityItem(
    record: RunningRecord,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(Color(0xFFF4F3EE).copy(alpha = 0.4f))
            .clickable { onClick() }
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Placeholder for map screenshot
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(11.dp))
                .background(Color(0xFFD9D9D9)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "🗺",
                fontSize = 24.sp
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = record.date,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = String.format("%.1f", record.distance / 1000),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "KM",
                    fontSize = 12.sp,
                    color = Color(0xFF0D120E)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = formatTime(record.duration),
                    fontSize = 12.sp,
                    color = Color(0xFF0D120E).copy(alpha = 0.3f)
                )
                Text(
                    text = "${record.paceString} min/km",
                    fontSize = 12.sp,
                    color = Color(0xFF0D120E).copy(alpha = 0.3f)
                )
                Text(
                    text = String.format("%.1f Kcal", record.calories),
                    fontSize = 12.sp,
                    color = Color(0xFF0D120E).copy(alpha = 0.3f)
                )
            }
        }
    }
}

private fun formatTime(seconds: Int): String {
    if (seconds <= 0) return "00:00:00"
    val h = seconds / 3600
    val m = (seconds % 3600) / 60
    val s = seconds % 60
    return String.format("%02d:%02d:%02d", h, m, s)
}
