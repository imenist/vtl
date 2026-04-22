package com.vitalo.markrun.ui.spinwheel

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.vitalo.markrun.R
import com.vitalo.markrun.ui.common.CommonLottieView
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

@Composable
fun SpinWheelScreen(
    navController: NavController
) {
    val scope = rememberCoroutineScope()

    var wheelOpacity by remember { mutableFloatStateOf(0f) }
    var isEntranceComplete by remember { mutableStateOf(false) }

    var isCountingDown by remember { mutableStateOf(false) }
    var isSpinning by remember { mutableStateOf(false) }
    var isCompleting by remember { mutableStateOf(false) }

    var rotationDeg by remember { mutableFloatStateOf(0f) }
    var selectedRewardIndex by remember { mutableStateOf<Int?>(null) }
    var flashOpacity by remember { mutableFloatStateOf(1f) }
    var showWinningLight by remember { mutableStateOf(false) }
    var showFingerGuide by remember { mutableStateOf(true) }
    var skipButtonClicked by remember { mutableStateOf(false) }
    var buttonScale by remember { mutableFloatStateOf(1f) }
    var buttonClickScale by remember { mutableFloatStateOf(1f) }

    var showResult by remember { mutableStateOf(false) }
    var resultCoinAmount by remember { mutableStateOf(0) }

    var spinJob by remember { mutableStateOf<Job?>(null) }
    var targetRotation by remember { mutableFloatStateOf(0f) }

    // Entrance Animation
    LaunchedEffect(Unit) {
        val opacityAnim = Animatable(0f)
        opacityAnim.animateTo(1f, animationSpec = tween(400, easing = LinearEasing)) {
            wheelOpacity = value
        }
        isEntranceComplete = true
        isCountingDown = true

        // Button Entrance Scale
        launch {
            Animatable(1f).animateTo(0.9f, tween(400)) { buttonScale = value }
            Animatable(0.9f).animateTo(1.1f, tween(600)) { buttonScale = value }
            Animatable(1.1f).animateTo(1f, tween(400)) { buttonScale = value }
        }

        // Start Auto Spin after countdown (2.8s)
        delay(2800)
        isCountingDown = false
    }

    val triggerSpin: () -> Unit = {
        if (!isSpinning && !isCompleting && !showResult) {
            isCountingDown = false
            isSpinning = true
            showFingerGuide = false

            scope.launch {
                Animatable(1f).animateTo(0.9f, tween(200)) { buttonClickScale = value }
                Animatable(0.9f).animateTo(1f, tween(200)) { buttonClickScale = value }
            }

            val targetIndex = Random.nextInt(0, 8)
            selectedRewardIndex = targetIndex
            resultCoinAmount = when (targetIndex) {
                0 -> 100
                1 -> 150
                2 -> 200
                3 -> 300
                4 -> 3000
                5 -> 500
                6 -> 1500
                7 -> 1000
                else -> 100
            }

            val sectorAngle = 360f / 8f
            val extraRotations = Random.nextInt(5, 8)
            val targetSectorCenter = ((8 - 1) - targetIndex) * sectorAngle + 2f
            targetRotation = extraRotations * 360f + targetSectorCenter

            spinJob?.cancel()
            spinJob = scope.launch {
                Animatable(rotationDeg).animateTo(
                    targetValue = targetRotation,
                    animationSpec = tween(5500, easing = CubicBezierEasing(0.05f, 0.0f, 0.05f, 1.0f))
                ) {
                    rotationDeg = value
                }

                if (!skipButtonClicked) {
                    isCompleting = true
                    showWinningLight = true
                    for (i in 0 until 4) {
                        Animatable(1f).animateTo(0f, tween(300, easing = LinearEasing)) { flashOpacity = value }
                        Animatable(0f).animateTo(1f, tween(300, easing = LinearEasing)) { flashOpacity = value }
                    }
                    showWinningLight = false
                    isSpinning = false
                    isCompleting = false
                    showResult = true
                    
                    // Fade out
                    Animatable(1f).animateTo(0f, tween(500, easing = LinearEasing)) { wheelOpacity = value }
                }
            }
        }
    }

    val triggerSkip: () -> Unit = {
        if (isSpinning && !skipButtonClicked && !isCompleting) {
            skipButtonClicked = true
            spinJob?.cancel()
            rotationDeg = targetRotation
            
            scope.launch {
                isCompleting = true
                showWinningLight = true
                for (i in 0 until 4) {
                    Animatable(1f).animateTo(0f, tween(300, easing = LinearEasing)) { flashOpacity = value }
                    Animatable(0f).animateTo(1f, tween(300, easing = LinearEasing)) { flashOpacity = value }
                }
                showWinningLight = false
                isSpinning = false
                isCompleting = false
                showResult = true

                // Fade out
                Animatable(1f).animateTo(0f, tween(500, easing = LinearEasing)) { wheelOpacity = value }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.8f))
    ) {
        // Main Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer { alpha = wheelOpacity },
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.weight(1f))

            // Titles
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(bottom = 13.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "Spin to Win Bonus！",
                        fontSize = 33.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFFEE7881),
                        style = LocalTextStyle.current.copy(
                            shadow = Shadow(color = Color(0xFFE5495D), offset = Offset(0f, 4f), blurRadius = 0f),
                            drawStyle = Stroke(miter = 10f, width = 2f, join = StrokeJoin.Round)
                        )
                    )
                    Text(
                        text = "Spin to Win Bonus！",
                        fontSize = 33.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFFF8D5CF)
                    )
                }

                Spacer(modifier = Modifier.height(15.dp))

                Text(
                    text = "100% Chance to Win",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(Color(0x80F46477), Color(0x80F4BEC5)),
                                start = Offset(0f, Float.POSITIVE_INFINITY),
                                end = Offset(0f, 0f)
                            ),
                            shape = RoundedCornerShape(50)
                        )
                        .border(2.dp, Color(0x80FCE6DD), RoundedCornerShape(50))
                        .padding(horizontal = 30.dp, vertical = 8.dp)
                )
            }

            // Wheel Area
            Box(modifier = Modifier.size(326.dp, 462.dp)) {
                // Spin Wheel Layers
                Box(
                    modifier = Modifier
                        .size(290.dp)
                        .align(Alignment.Center)
                        .offset(y = (-51).dp)
                        .graphicsLayer { rotationZ = rotationDeg }
                ) {
                    Image(
                        painter = painterResource(R.drawable.img_spin_background),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize()
                    )
                    for (i in 0 until 8) {
                        val angle = i * 45f
                        val angleInRad = Math.toRadians((angle - 90f + 22.5f).toDouble())
                        val radius = 80.dp.value
                        val offsetX = (cos(angleInRad) * radius).dp
                        val offsetY = (sin(angleInRad) * radius).dp
                        val isWinning = selectedRewardIndex == i
                        val currentOpacity = if (isWinning) flashOpacity else 1f

                        Image(
                            painter = painterResource(getRewardImageId(i)),
                            contentDescription = null,
                            modifier = Modifier
                                .size(85.dp, 97.dp)
                                .align(Alignment.Center)
                                .offset(x = offsetX, y = offsetY)
                                .graphicsLayer {
                                    rotationZ = angle + 22.5f
                                    alpha = currentOpacity
                                }
                        )
                    }
                }

                // Lottie Border
                CommonLottieView(
                    animationName = "SpinBorder",
                    loop = true,
                    modifier = Modifier.fillMaxSize()
                )

                // Winning Light
                if (showWinningLight) {
                    CommonLottieView(
                        animationName = "SpinLightAward",
                        loop = true,
                        modifier = Modifier
                            .size(87.dp, 82.dp)
                            .align(Alignment.TopCenter)
                            .offset(x = (-39.5).dp, y = 54.dp)
                    )
                }

                // Center Button
                Box(
                    modifier = Modifier
                        .size(119.dp)
                        .align(Alignment.Center)
                        .offset(y = (-51).dp)
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() },
                            enabled = !isSpinning && !isCompleting && !showResult
                        ) {
                            triggerSpin()
                        }
                        .graphicsLayer {
                            scaleX = buttonClickScale
                            scaleY = buttonClickScale
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(R.drawable.center_count_spin),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize()
                    )
                    CountdownNumberView(isEntranceComplete, isCountingDown, isSpinning)
                }

                // Spin Now Button
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 41.5.dp)
                        .size(230.dp, 56.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clickable(
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() },
                                enabled = !isSpinning && !isCompleting && !showResult
                            ) {
                                triggerSpin()
                            }
                            .graphicsLayer {
                                scaleX = buttonClickScale
                                scaleY = buttonClickScale
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        CommonLottieView(
                            animationName = "SpinNowButton",
                            loop = false,
                            modifier = Modifier
                                .fillMaxSize()
                                .graphicsLayer {
                                    scaleX = buttonScale
                                    scaleY = buttonScale
                                }
                        )
                        Text(
                            text = if (isSpinning || isCompleting || showResult) "SPIN IN..." else "SPIN NOW",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            fontStyle = FontStyle.Italic,
                            color = Color.White,
                            modifier = Modifier.graphicsLayer {
                                alpha = if (isSpinning || isCompleting || showResult) 0.2f else 1f
                            }
                        )
                    }

                    if (showFingerGuide) {
                        FingerGuideView(isEntranceComplete) {
                            showFingerGuide = false
                        }
                    }
                }
            }

            Text(
                text = "The activity has nothing to do with Apple inc.",
                fontSize = 12.sp,
                color = Color.White,
                modifier = Modifier.padding(top = 9.dp)
            )

            Spacer(modifier = Modifier.weight(1f))
        }

        // Top Buttons (Close / Skip)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .graphicsLayer { alpha = wheelOpacity }
        ) {
            if (!isSpinning) {
                Image(
                    painter = painterResource(R.drawable.icon_game_close),
                    contentDescription = "Close",
                    modifier = Modifier
                        .padding(start = 11.dp, top = 13.dp)
                        .size(30.dp)
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) {
                            navController.popBackStack()
                        }
                )
            }

            if (isSpinning && !skipButtonClicked && !isCompleting) {
                Image(
                    painter = painterResource(R.drawable.ic_spin_skip),
                    contentDescription = "Skip",
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(end = 11.dp, top = 13.dp)
                        .size(62.dp, 30.dp)
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) {
                            triggerSkip()
                        }
                )
            }
        }

        if (showResult) {
            SpinWheelRewardDialog(
                coinNum = resultCoinAmount,
                onClose = {
                    showResult = false
                    navController.popBackStack()
                },
                onPlayAgain = {
                    showResult = false
                    isEntranceComplete = true
                    wheelOpacity = 1f
                    rotationDeg = 0f
                    targetRotation = 0f
                    skipButtonClicked = false
                    isSpinning = false
                    isCompleting = false
                    // Short delay to reset then auto spin again
                    scope.launch {
                        delay(100)
                        triggerSpin()
                    }
                }
            )
        }
    }
}

@Composable
fun FingerGuideView(isEntranceComplete: Boolean, onComplete: () -> Unit) {
    var scale by remember { mutableFloatStateOf(1f) }
    var alpha by remember { mutableFloatStateOf(0f) }
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(isEntranceComplete) {
        if (isEntranceComplete) {
            // First loop
            launch { Animatable(1f).animateTo(0.8f, tween(400)) { scale = value } }
            launch { Animatable(0f).animateTo(1f, tween(400)) { alpha = value } }
            launch { Animatable(0f).animateTo(-16.5f, tween(400)) { offsetX = value } }
            launch { Animatable(0f).animateTo(-18.5f, tween(400)) { offsetY = value } }

            delay(400)

            launch { Animatable(0.8f).animateTo(1f, tween(600)) { scale = value } }
            launch { Animatable(1f).animateTo(0f, tween(600)) { alpha = value } }
            launch { Animatable(-16.5f).animateTo(0f, tween(600)) { offsetX = value } }
            launch { Animatable(-18.5f).animateTo(0f, tween(600)) { offsetY = value } }

            delay(600)

            // Second loop
            launch { Animatable(1f).animateTo(0.8f, tween(400)) { scale = value } }
            launch { Animatable(0f).animateTo(1f, tween(400)) { alpha = value } }
            launch { Animatable(0f).animateTo(-16.5f, tween(400)) { offsetX = value } }
            launch { Animatable(0f).animateTo(-18.5f, tween(400)) { offsetY = value } }

            delay(400)

            launch { Animatable(0.8f).animateTo(1f, tween(600)) { scale = value } }
            launch { Animatable(1f).animateTo(0f, tween(600)) { alpha = value } }
            launch { Animatable(-16.5f).animateTo(0f, tween(600)) { offsetX = value } }
            launch { Animatable(-18.5f).animateTo(0f, tween(600)) { offsetY = value } }

            delay(600)
            onComplete()
        }
    }

    Image(
        painter = painterResource(R.drawable.ic_finger_guide),
        contentDescription = null,
        modifier = Modifier
            .offset(x = 110.dp, y = 42.5.dp)
            .size(113.dp, 99.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                translationX = offsetX.dp.toPx()
                translationY = offsetY.dp.toPx()
                this.alpha = alpha
            }
    )
}

@Composable
fun CountdownNumberView(isEntranceComplete: Boolean, isCountingDown: Boolean, isSpinning: Boolean) {
    if (!isCountingDown || isSpinning) return
    Box(contentAlignment = Alignment.Center) {
        CountdownNumber(3, 0, isEntranceComplete)
        CountdownNumber(2, 700, isEntranceComplete)
        CountdownNumber(1, 1400, isEntranceComplete)
    }
}

@Composable
fun CountdownNumber(number: Int, delayMs: Long, isEntranceComplete: Boolean) {
    var scale by remember { mutableFloatStateOf(0.44f) }
    var alpha by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(isEntranceComplete) {
        if (isEntranceComplete) {
            delay(delayMs)
            delay(400)

            // Entrance
            launch { Animatable(0.44f).animateTo(1f, tween(460, easing = FastOutSlowInEasing)) { scale = value } }
            launch { Animatable(0f).animateTo(1f, tween(460, easing = FastOutSlowInEasing)) { alpha = value } }

            delay(460 + 240)

            // Exit
            launch { Animatable(1f).animateTo(1.26f, tween(300, easing = FastOutSlowInEasing)) { scale = value } }
            launch { Animatable(1f).animateTo(0f, tween(300, easing = FastOutSlowInEasing)) { alpha = value } }
        }
    }

    if (alpha > 0f) {
        Text(
            text = number.toString(),
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            fontStyle = FontStyle.Italic,
            color = Color.White,
            modifier = Modifier.graphicsLayer {
                scaleX = scale
                scaleY = scale
                this.alpha = alpha
            }
        )
    }
}

fun getRewardImageId(index: Int): Int {
    return when (index) {
        0 -> R.drawable.img_reward_1
        1 -> R.drawable.img_reward_2
        2 -> R.drawable.img_reward_3
        3 -> R.drawable.img_reward_4
        4 -> R.drawable.img_reward_5
        5 -> R.drawable.img_reward_6
        6 -> R.drawable.img_reward_7
        7 -> R.drawable.img_reward_8
        else -> R.drawable.img_reward_1
    }
}

@Composable
private fun SpinWheelRewardDialog(
    coinNum: Int,
    onClose: () -> Unit,
    onPlayAgain: () -> Unit
) {
    var contentOpacity by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(Unit) {
        // Match the Lottie 0.5 progress fade-in logic
        delay(800) // approx half progress of the result animation
        Animatable(0f).animateTo(1f, tween(800)) { contentOpacity = value }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.7f))
    ) {
        // Full screen Lottie
        CommonLottieView(
            animationName = "SpinWheelResult",
            loop = false,
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.Center)
        )

        // Dialog Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer { alpha = contentOpacity },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(100.dp))

            Text(
                text = "Congrats!",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                fontStyle = FontStyle.Italic,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(23.dp))

            // 金币与汇率换算背景 bg_spin_exchange (placeholder color if missing)
            Box(
                modifier = Modifier
                    .size(240.dp, 80.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0x33FFFFFF)), // Placeholder for bg_spin_exchange
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_coin_arrived_dialog_coin),
                        contentDescription = null,
                        modifier = Modifier.size(40.dp),
                        contentScale = ContentScale.Fit
                    )
                    Text(
                        text = coinNum.toString(),
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFFD700),
                        modifier = Modifier.padding(start = 4.dp, end = 12.dp)
                    )
                    
                    Text(
                        text = "=",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )

                    Text(
                        text = String.format(java.util.Locale.US, "US$%.2f", coinNum * 0.01),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(start = 12.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(47.dp))

            // Claim Button with Lottie
            Box(
                modifier = Modifier
                    .size(230.dp, 60.dp)
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) { onPlayAgain() },
                contentAlignment = Alignment.Center
            ) {
                CommonLottieView(
                    animationName = "SpinWheelAdBtn",
                    loop = true,
                    modifier = Modifier.size(270.dp, 107.dp)
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 5.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_exchange_ad_play),
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        contentScale = ContentScale.Fit
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Claim x 1.2",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        fontStyle = FontStyle.Italic,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(66.dp))

            // Close Button
            Image(
                painter = painterResource(id = R.drawable.ic_coin_arrived_dialog_close),
                contentDescription = null,
                modifier = Modifier
                    .size(44.dp)
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) { onClose() }
            )
        }
    }
}
