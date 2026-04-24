package com.vitalo.markrun.ui.lesson

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
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
import com.vitalo.markrun.R
import com.vitalo.markrun.navigation.Screen
import com.vitalo.markrun.ui.common.CoinBalanceView
import com.vitalo.markrun.ui.common.CommonEmptyView
import com.vitalo.markrun.ui.common.CommonErrorView
import com.vitalo.markrun.ui.common.CommonLoadingView
import com.vitalo.markrun.ui.common.CommonLottieView
import com.vitalo.markrun.ui.common.GlobalOverlayManager
import com.vitalo.markrun.ui.stepcounter.StepCounterView
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LessonScreen(
    navController: NavController,
    viewModel: LessonViewModel = hiltViewModel(),
    onShowSignIn: () -> Unit = {},
    onNavigateToWebGame: (String) -> Unit = {},
    onTapExchange: () -> Unit = {},
    onTapEarnRules: () -> Unit = {}
) {
    val subjects by viewModel.subjects.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val isError by viewModel.isError.collectAsState()

    LaunchedEffect(Unit) {
        if (subjects.isEmpty() && !isLoading) {
            viewModel.loadInitialData()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Image(
            painter = painterResource(R.drawable.img_lesson_bg),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        when {
            subjects.isNotEmpty() -> {
                PullToRefreshBox(
                    isRefreshing = isLoading,
                    onRefresh = { viewModel.reload() },
                    modifier = Modifier.statusBarsPadding()
                ) {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        item {
                            LessonHeader(
                                navController = navController,
                                onShowSignIn = onShowSignIn,
                                onNavigateToWebGame = onNavigateToWebGame,
                                onTapExchange = onTapExchange,
                                onTapEarnRules = onTapEarnRules
                            )
                        }

                        itemsIndexed(subjects) { _, subject ->
                            SubjectSectionView(
                                subject = subject,
                                onTrainingTap = { training, partIndex ->
                                    navController.navigate(
                                        Screen.LessonDetail.createRoute(
                                            trainingCode = training.code,
                                            partIndex = partIndex + 1,
                                            fromToday = false
                                        )
                                    )
                                }
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                        }

                        item {
                            Spacer(modifier = Modifier.height(20.dp))
                        }
                    }
                }
            }
            isLoading -> CommonLoadingView()
            isError -> CommonErrorView(onRetry = { viewModel.reload() })
            else -> CommonEmptyView()
        }
    }
}

@Composable
private fun LessonHeader(
    navController: NavController,
    onShowSignIn: () -> Unit,
    onNavigateToWebGame: (String) -> Unit,
    onTapExchange: () -> Unit,
    onTapEarnRules: () -> Unit
) {
    Box(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(72.dp))

            StepCounterView()

            Spacer(modifier = Modifier.height(10.dp))
        }

        TopBarContent(
            navController = navController,
            onTapExchange = onTapExchange,
            onTapEarnRules = onTapEarnRules,
            onNavigateToWebGame = onNavigateToWebGame
        )

        AdSlotMachineButton(
            modifier = Modifier
                .padding(top = 90.dp, start = 12.5.dp)
                .align(Alignment.TopStart),
            onTap = { onNavigateToWebGame("slotMachine") }
        )

        AdSmashEggButton(
            modifier = Modifier
                .padding(top = 250.dp, start = 12.5.dp)
                .align(Alignment.TopStart),
            onTap = { onNavigateToWebGame("smashEgg") }
        )

        AdLuckySpinButton(
            modifier = Modifier
                .padding(top = 70.dp, end = 6.dp)
                .align(Alignment.TopEnd),
            onTap = { GlobalOverlayManager.showSpinWheelOverlay() }
        )

        DailySignEntranceButton(
            modifier = Modifier
                .padding(top = 155.dp, end = 8.dp)
                .align(Alignment.TopEnd),
            onTap = onShowSignIn
        )
    }
}

@Composable
private fun TopBarContent(
    navController: NavController,
    onTapExchange: () -> Unit,
    onTapEarnRules: () -> Unit,
    onNavigateToWebGame: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 7.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CoinBalanceView(
            showExchange = true,
            coinExchangeRate = 1000.0,
            onTapExchange = onTapExchange,
            modifier = Modifier.offset(x = (-1).dp)
        )

        Spacer(modifier = Modifier.weight(1f))

        AdGamePlayButton(
            modifier = Modifier.padding(end = 6.dp),
            onTap = { onNavigateToWebGame("game") }
        )

        RulesButton(onClick = onTapEarnRules)

        Spacer(modifier = Modifier.width(8.dp))

        MineButton(onClick = { navController.navigate(Screen.Mine.route) })

        Spacer(modifier = Modifier.width(16.dp))
    }
}

@Composable
private fun RulesButton(onClick: () -> Unit) {
    Image(
        painter = painterResource(id = R.drawable.ic_earn_rules),
        contentDescription = "Rules",
        modifier = Modifier
            .size(34.dp)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) { onClick() },
        contentScale = ContentScale.Fit
    )
}

@Composable
private fun MineButton(onClick: () -> Unit) {
    Image(
        painter = painterResource(id = R.drawable.ic_personal),
        contentDescription = "Profile",
        modifier = Modifier
            .size(34.dp)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) { onClick() },
        contentScale = ContentScale.Fit
    )
}

@Composable
private fun AdGamePlayButton(
    modifier: Modifier = Modifier,
    onTap: () -> Unit
) {
    Box(
        modifier = modifier.clickable { onTap() },
        contentAlignment = Alignment.BottomCenter
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_lesson_ad_entrance_game),
            contentDescription = "Play",
            modifier = Modifier.size(60.dp),
            contentScale = ContentScale.Fit
        )

        Box(
            modifier = Modifier
                .padding(bottom = 13.dp, start = 2.dp)
                .height(14.dp)
                .clip(RoundedCornerShape(7.dp))
                .background(Color(0xFFD3D1FF))
                .padding(horizontal = 7.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Play",
                fontSize = 9.sp,
                fontWeight = FontWeight.Black,
                fontStyle = FontStyle.Italic,
                color = Color(0xFF0D120E),
                maxLines = 1
            )
        }
    }
}

@Composable
private fun AdSlotMachineButton(
    modifier: Modifier = Modifier,
    onTap: () -> Unit
) {
    Box(
        modifier = modifier.clickable { onTap() },
        contentAlignment = Alignment.BottomCenter
    ) {
        CommonLottieView(
            animationName = "SlotEntrance",
            loop = true,
            modifier = Modifier.size(63.dp)
        )

        Text(
            text = "老虎机",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier
                .offset(y = (-5).dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFFB97D7))
                .padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}

@Composable
private fun AdSmashEggButton(
    modifier: Modifier = Modifier,
    onTap: () -> Unit
) {
    Box(
        modifier = modifier.clickable { onTap() },
        contentAlignment = Alignment.BottomCenter
    ) {
        CommonLottieView(
            animationName = "SmashEggEntrance",
            loop = true,
            modifier = Modifier.size(63.dp)
        )

        Text(
            text = "砸金蛋",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier
                .offset(y = (-3).dp)
                .clip(RoundedCornerShape(9.dp))
                .background(Color(0xFFFF753D))
                .padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}

@Composable
private fun AdLuckySpinButton(
    modifier: Modifier = Modifier,
    onTap: () -> Unit
) {
    Box(
        modifier = modifier.clickable { onTap() },
        contentAlignment = Alignment.BottomCenter
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_lesson_ad_entrance_spin),
            contentDescription = "Spin",
            modifier = Modifier.size(70.dp),
            contentScale = ContentScale.Fit
        )

        Text(
            text = "赢奖励",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier
                .offset(y = ((-5).dp))
                .clip(RoundedCornerShape(15.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFFFF4A4A), Color(0xFFFF8A33))
                    )
                )
                .padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}

@Composable
private fun DailySignEntranceButton(
    modifier: Modifier = Modifier,
    onTap: () -> Unit
) {
    Box(
        modifier = modifier
            .width(56.dp)
            .height(58.dp)
            .clickable { onTap() },
        contentAlignment = Alignment.TopCenter
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_daily_sign_bg),
            contentDescription = null,
            modifier = Modifier
                .width(56.dp)
                .height(58.dp),
            contentScale = ContentScale.Fit
        )

        Image(
            painter = painterResource(id = R.drawable.ic_daily_sign),
            contentDescription = null,
            modifier = Modifier
                .padding(top = 10.dp)
                .size(33.dp),
            contentScale = ContentScale.Fit
        )

        Text(
            text = "签到",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier
                .padding(top = 38.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFF2BADF4))
                .padding(horizontal = 4.dp, vertical = 1.dp)
        )

        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 13.dp, end = 10.dp)
                .size(10.dp)
                .clip(CircleShape)
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(7.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE35C26))
            )
        }
    }
}

