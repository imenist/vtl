package com.vitalo.markrun.ui.running

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.android.gms.maps.model.LatLng
import com.vitalo.markrun.R
import com.vitalo.markrun.data.local.db.entity.RunningRecord
import com.vitalo.markrun.navigation.Screen
import com.vitalo.markrun.ui.theme.DarkBackground

@Composable
fun RunningResultScreen(
    navController: NavController,
    recordId: Long,
    fragmentsCount: Int,
    viewModel: RunningResultViewModel = hiltViewModel()
) {
    val record by viewModel.record.collectAsState()
    val routePoints by viewModel.routePoints.collectAsState()

    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(recordId) {
        viewModel.loadRecord(recordId)
    }

    val workout = record ?: return

    val mapPoints = remember(routePoints) {
        routePoints.map { LatLng(it.latitude, it.longitude) }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        colors = listOf(Color.White, Color(0xFFFFFFE9))
                    )
                )
                .blur(if (showDeleteDialog) 5.dp else 0.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Top bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_close),
                    contentDescription = "Close",
                    modifier = Modifier
                        .size(50.dp)
                        .clickable {
                            navController.navigate(Screen.Home.route) {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                )
                Image(
                    painter = painterResource(id = R.drawable.ic_delete),
                    contentDescription = "Delete",
                    modifier = Modifier
                        .size(50.dp)
                        .clickable { showDeleteDialog = true }
                )
            }

            // Medal section
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "GOOD!!",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontStyle = FontStyle.Italic
                )

                Image(
                    painter = painterResource(id = R.drawable.img_running_result_medal),
                    contentDescription = null,
                    modifier = Modifier.size(169.dp),
                    contentScale = ContentScale.Fit
                )
            }

            // Running data card
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(top = 35.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFFF4F3EE).copy(alpha = 0.4f))
                    .padding(horizontal = 29.dp, vertical = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "TIME",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF757575)
                        )
                        Text(
                            text = formatTime(workout.duration),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF0D120E)
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = "PACE (min/km)",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF757575)
                        )
                        Text(
                            text = workout.paceString,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF0D120E)
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "DISTANCE(KM)",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF757575)
                        )
                        Text(
                            text = String.format("%.1f", workout.distance / 1000),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF0D120E)
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = "KCAL",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF757575)
                        )
                        Text(
                            text = String.format("%.2f", workout.calories),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF0D120E)
                        )
                    }
                }
            }

            // Map section
            if (mapPoints.size >= 2) {
                RunningMapView(
                    locations = mapPoints,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(198.dp)
                        .padding(horizontal = 20.dp)
                        .padding(top = 10.dp)
                        .clip(RoundedCornerShape(20.dp)),
                    isFollowingUser = false,
                    fitToRoute = true
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Close button
            Button(
                onClick = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .padding(horizontal = 38.dp),
                shape = RoundedCornerShape(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = DarkBackground)
            ) {
                Text(
                    text = "GET",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    fontStyle = FontStyle.Italic,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }

        if (showDeleteDialog) {
            DeleteRecordDialog { isDelete ->
                showDeleteDialog = false
                if (isDelete) {
                    viewModel.deleteRecord(recordId)
                    navController.navigate(Screen.Home.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            }
        }
    }
}

private fun formatTime(seconds: Int): String {
    if (seconds <= 0) return "00:00:00"
    val h = seconds / 3600
    val m = (seconds % 3600) / 60
    val s = seconds % 60
    return if (h > 0) {
        String.format("%02d:%02d:%02d", h, m, s)
    } else {
        String.format("%02d:%02d", m, s)
    }
}
