package com.vitalo.markrun.ui.followalong

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.vitalo.markrun.ad.AdManager
import com.vitalo.markrun.ad.Ads
import com.vitalo.markrun.navigation.Screen
import com.vitalo.markrun.ui.common.GlobalOverlayManager
import com.vitalo.markrun.ui.common.TrainingPackageDialog
import com.vitalo.markrun.ui.theme.DarkBackground
import com.vitalo.markrun.ui.theme.GradientGreenEnd
import com.vitalo.markrun.ui.theme.GradientGreenStart
import kotlinx.coroutines.delay

@Composable
fun WorkoutResultScreen(
    navController: NavController,
    duration: Int = 0,
    calorie: Int = 0,
    hasCompleted: Boolean = false,
    coins: Int = 0,
    viewModel: WorkoutResultViewModel = hiltViewModel()
) {
    val navBarHeight = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    val hasReward = duration > 0 || calorie > 0 || hasCompleted
    
    val rewardVirtualId = if (hasReward) Ads.REWARD_COURSE_FINISH else Ads.REWARD_COURSE_MID_END
    val adEnable = remember { viewModel.isAdAvailable(rewardVirtualId) }
    val adReward = remember { viewModel.getAdReward(rewardVirtualId) }
    
    var showTrainingPackageDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if (adEnable && viewModel.isLargeWithdrawEnable()) {
            delay(500)
            showTrainingPackageDialog = true
        }
    }

    if (showTrainingPackageDialog) {
        TrainingPackageDialog(
            coinNum = adReward,
            titleText = if (hasReward) "Congratulations!" else "Training Package",
            claimButtonText = "Claim Reward",
            showAdIcon = true,
            onTapClaim = {
                showTrainingPackageDialog = false
                val activity = navController.context as? android.app.Activity
                if (activity != null) {
                    AdManager.showAd(
                        activity = activity,
                        virtualId = rewardVirtualId,
                        onComplete = { rewarded ->
                            if (rewarded) {
                                viewModel.addCoins(adReward)
                                GlobalOverlayManager.showCoinArrivedOverlay(adReward)
                            }
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
            onTapClose = {
                showTrainingPackageDialog = false
            }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                if (hasReward) {
                    Brush.verticalGradient(
                        colors = listOf(GradientGreenEnd, GradientGreenStart)
                    )
                } else {
                    Brush.verticalGradient(
                        colors = listOf(Color.White, Color.White)
                    )
                }
            )
    ) {
        // Top Left Close Button
        IconButton(
            onClick = {
                val activity = navController.context as? android.app.Activity
                if (activity != null) {
                    AdManager.showAd(
                        activity = activity,
                        virtualId = Ads.INTERSTITIAL_COURSE_END,
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
            modifier = Modifier
                .padding(top = 40.dp, start = 16.dp)
                .align(Alignment.TopStart)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Close",
                tint = DarkBackground,
                modifier = Modifier.size(28.dp)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 37.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(0.25f))

            if (!hasReward) {
                Text(
                    text = "😥",
                    fontSize = 96.sp
                )
            }

            Spacer(modifier = Modifier.height(if (hasReward) 40.dp else 24.dp))

            Text(
                text = if (hasReward) "Congratulations!" else "End of Training",
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                fontStyle = FontStyle.Italic,
                color = DarkBackground,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = if (hasReward)
                    "Your Training is complete, here's your reward!"
                else
                    "Training ended early. No rewards were received.",
                fontSize = 16.sp,
                color = DarkBackground.copy(alpha = 0.9f),
                textAlign = TextAlign.Center,
                modifier = Modifier.width(235.dp)
            )

            if (coins > 0) {
                Spacer(modifier = Modifier.height(30.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "🪙",
                        fontSize = 32.sp
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "+$coins CaloCoins",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFF6645)
                    )
                }
            }

            Spacer(modifier = Modifier.height(if (coins > 0) 38.dp else 68.dp))

            // Stats
            Row(
                horizontalArrangement = Arrangement.spacedBy(50.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Duration
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("⏱", fontSize = 20.sp)
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "$duration",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Medium,
                        color = DarkBackground
                    )
                    Text(
                        text = "MIN",
                        fontSize = 12.sp,
                        color = DarkBackground
                    )
                }

                // Divider
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(32.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color.Black.copy(alpha = 0.1f))
                )

                // Calories
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("🔥", fontSize = 20.sp)
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "$calorie",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Medium,
                        color = DarkBackground
                    )
                    Text(
                        text = "KCAL",
                        fontSize = 12.sp,
                        color = DarkBackground
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            if (adEnable) {
                // Reward button
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                        .clip(RoundedCornerShape(50.dp))
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(Color(0xFFFD4D82), Color(0xFFFF8F45))
                            )
                        )
                        .clickable {
                            val activity = navController.context as? android.app.Activity
                            if (activity != null) {
                                AdManager.showAd(
                                    activity = activity,
                                    virtualId = rewardVirtualId,
                                    onComplete = { rewarded ->
                                        if (rewarded) {
                                            viewModel.addCoins(adReward)
                                            GlobalOverlayManager.showCoinArrivedOverlay(adReward)
                                        }
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
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "🪙 +$adReward",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            fontStyle = FontStyle.Italic,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "▶",
                            fontSize = 18.sp
                        )
                    }
                }
            } else {
                // Thanks / Done button
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                        .clip(RoundedCornerShape(50.dp))
                        .background(DarkBackground)
                        .clickable {
                            val activity = navController.context as? android.app.Activity
                            if (activity != null) {
                                AdManager.showAd(
                                    activity = activity,
                                    virtualId = Ads.INTERSTITIAL_COURSE_END,
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
                    Text(
                        text = "THANKS",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        fontStyle = FontStyle.Italic,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp + navBarHeight))
        }
    }
}
