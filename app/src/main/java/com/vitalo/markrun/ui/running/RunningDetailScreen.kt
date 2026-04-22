package com.vitalo.markrun.ui.running

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Text
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.android.gms.maps.model.LatLng

@Composable
fun RunningDetailScreen(
    navController: NavController,
    recordId: Long,
    viewModel: RunningResultViewModel = hiltViewModel()
) {
    val record by viewModel.record.collectAsState()
    val routePoints by viewModel.routePoints.collectAsState()

    LaunchedEffect(recordId) {
        viewModel.loadRecord(recordId)
    }

    val workout = record ?: return

    val mapPoints = remember(routePoints) {
        routePoints.map { LatLng(it.latitude, it.longitude) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    colors = listOf(Color.White, Color(0xFFFFFFE9))
                )
            )
            .verticalScroll(rememberScrollState())
    ) {
        RunningTitleBar(
            title = "Running Detail",
            onBack = { navController.popBackStack() }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Map
        if (mapPoints.size >= 2) {
            RunningMapView(
                locations = mapPoints,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .padding(horizontal = 20.dp)
                    .clip(RoundedCornerShape(20.dp)),
                isFollowingUser = false,
                fitToRoute = true
            )

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Stats card
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFFF4F3EE).copy(alpha = 0.4f))
                .padding(horizontal = 24.dp, vertical = 20.dp)
        ) {
            DetailRow(label = "Distance", value = String.format("%.2f km", workout.distance / 1000))
            Spacer(modifier = Modifier.height(12.dp))
            DetailRow(label = "Duration", value = formatDetailTime(workout.duration))
            Spacer(modifier = Modifier.height(12.dp))
            DetailRow(label = "Pace", value = workout.paceString)
            Spacer(modifier = Modifier.height(12.dp))
            DetailRow(label = "Calories", value = String.format("%.1f kcal", workout.calories))
            Spacer(modifier = Modifier.height(12.dp))
            DetailRow(label = "Avg Speed", value = String.format("%.1f km/h", workout.speed * 3.6))
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Intensity breakdown
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFFF4F3EE).copy(alpha = 0.4f))
                .padding(horizontal = 24.dp, vertical = 20.dp)
        ) {
            Text(
                text = "Intensity Breakdown",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF0D120E)
            )
            Spacer(modifier = Modifier.height(12.dp))
            DetailRow(label = "Low", value = formatDetailTime(workout.lowIntensityTime))
            Spacer(modifier = Modifier.height(8.dp))
            DetailRow(label = "Moderate", value = formatDetailTime(workout.moderateIntensityTime))
            Spacer(modifier = Modifier.height(8.dp))
            DetailRow(label = "High", value = formatDetailTime(workout.highIntensityTime))
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = Color(0xFF757575)
        )
        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF0D120E)
        )
    }
}

private fun formatDetailTime(seconds: Int): String {
    if (seconds <= 0) return "00:00"
    val h = seconds / 3600
    val m = (seconds % 3600) / 60
    val s = seconds % 60
    return if (h > 0) {
        String.format("%02d:%02d:%02d", h, m, s)
    } else {
        String.format("%02d:%02d", m, s)
    }
}
