package com.vitalo.markrun.ui.running

import android.app.Activity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.android.gms.maps.model.LatLng
import com.vitalo.markrun.R
import com.vitalo.markrun.ad.AdManager
import com.vitalo.markrun.ad.Ads
import com.vitalo.markrun.data.local.db.entity.RunningRecord
import com.vitalo.markrun.navigation.Screen
import com.vitalo.markrun.ui.common.GlobalOverlayManager
import com.vitalo.markrun.ui.theme.DarkBackground
import kotlinx.coroutines.delay
import com.vitalo.markrun.ui.common.TrainingPackageDialog

@Composable
fun RunningResultScreen(
    navController: NavController,
    recordId: Long,
    fragmentsCount: Int,
    viewModel: RunningResultViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val record by viewModel.record.collectAsState()
    val routePoints by viewModel.routePoints.collectAsState()

    var showDeleteDialog by remember { mutableStateOf(false) }
    var showTrainingPackageDialog by remember { mutableStateOf(false) }

    LaunchedEffect(recordId) {
        viewModel.loadRecord(recordId)
        delay(500)
        showTrainingPackageDialog = true
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
                .statusBarsPadding()
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
                    contentDescription = null,
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

            // Reward section
            val totalCoins = workout.coins + (workout.adCoins ?: 0)
            if (fragmentsCount <= 0 && totalCoins <= 0) {
                Column(
                    modifier = Modifier.fillMaxWidth().offset(y = (-15).dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "你真棒！",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        fontStyle = FontStyle.Italic,
                        color = Color.Black
                    )
                    Image(
                        painter = painterResource(id = R.drawable.img_running_result_medal),
                        contentDescription = null,
                        modifier = Modifier.size(169.dp),
                        contentScale = ContentScale.Fit
                    )
                }
            } else {
                Column(
                    modifier = Modifier.fillMaxWidth().offset(y = (-15).dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "你真棒！",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        fontStyle = FontStyle.Italic,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(28.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(35.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (fragmentsCount > 0) {
                            RunningFragmentRewardView(fragmentNum = fragmentsCount)
                        }
                        if (totalCoins > 0) {
                            RunningCoinRewardView(coinNum = totalCoins)
                        }
                    }
                }
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
            if (mapPoints.isNotEmpty()) {
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

            // Get or Ad Button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 38.dp)
                    .height(64.dp)
                    .clip(RoundedCornerShape(50.dp))
                    .background(DarkBackground)
                    .clickable {
                        val activity = context as? Activity
                        if (activity != null) {
                            AdManager.showAd(
                                activity = activity,
                                virtualId = Ads.REWARD_RUN_END,
                                onComplete = {
                                    navController.navigate(Screen.Home.route) {
                                        popUpTo(0) { inclusive = true }
                                    }
                                }
                            )
                        } else {
                            navController.navigate(Screen.Home.route) {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_exchange_ad_play),
                        contentDescription = null,
                        modifier = Modifier.size(17.dp, 14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "继续收获",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        fontStyle = FontStyle.Italic,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }

        if (showTrainingPackageDialog) {
            TrainingPackageDialog(
                coinNum = 100,
                onTapClaim = { 
                    val activity = context as? Activity
                    if (activity != null) {
                        AdManager.showAd(
                            activity = activity,
                            virtualId = Ads.REWARD_RUN_END,
                            onComplete = { rewarded ->
                                showTrainingPackageDialog = false
                                if (rewarded) GlobalOverlayManager.showCoinArrivedOverlay(100)
                            }
                        )
                    } else {
                        showTrainingPackageDialog = false
                    }
                },
                onTapClose = { showTrainingPackageDialog = false }
            )
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

@Composable
private fun RunningFragmentRewardView(fragmentNum: Int) {
    Box(contentAlignment = Alignment.TopCenter) {
        Image(
            painter = painterResource(id = R.drawable.img_lesson_fragment_n_coin), // Replace with actual fragment image if available
            contentDescription = null,
            modifier = Modifier.size(115.dp),
            contentScale = ContentScale.Crop
        )
        Text(
            text = "x $fragmentNum",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            fontStyle = FontStyle.Italic,
            color = Color.White,
            modifier = Modifier.offset(x = 35.dp, y = 5.dp)
        )
    }
}

@Composable
private fun RunningCoinRewardView(coinNum: Int) {
    Box(contentAlignment = Alignment.TopCenter) {
        Image(
            painter = painterResource(id = R.drawable.img_workout_result_coin),
            contentDescription = null,
            modifier = Modifier.size(115.dp),
            contentScale = ContentScale.Crop
        )
        Text(
            text = "x $coinNum",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            fontStyle = FontStyle.Italic,
            color = Color.White,
            modifier = Modifier.offset(x = 35.dp, y = 5.dp)
        )
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
