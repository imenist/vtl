package com.vitalo.markrun.ui.common

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.activity.compose.BackHandler
import com.vitalo.markrun.R
import java.text.NumberFormat
import java.util.Locale

@Composable
fun CoinArrivedWithProgressDialog(
    coinNum: Int,
    desc: String = "CaloCoins Have Arrived",
    getButtonText: String,
    showBalance: Boolean = true,
    showAdButton: Boolean = false,
    showFingerGuide: Boolean = false,
    onClose: (Boolean) -> Unit = {},
    onAdButtonTap: () -> Unit = {},
    coinBalanceViewModel: CoinBalanceViewModel = hiltViewModel()
) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val lottieWidth = screenWidth
    val lottieHeight = lottieWidth / (938f / 1571f)
    val scaleX = (screenWidth.value / 375f)
    val scaleY = (lottieHeight.value / 628f)

    val currentCoin by coinBalanceViewModel.coinBalance.collectAsState()
    val coinExchangeRate = 1000.0
    val targetCash = 30.0
    val targetCoin = targetCash * coinExchangeRate
    val needCoin = targetCoin - currentCoin
    val percentage = if (needCoin > 0) (currentCoin / targetCoin).toFloat() else 1f

    var progress by remember { mutableFloatStateOf(0f) }
    var animatedBalance by remember { mutableDoubleStateOf(maxOf(0.0, currentCoin - coinNum)) }

    LaunchedEffect(Unit) {
        if (showBalance) {
            val start = maxOf(0.0, currentCoin - coinNum)
            val end = currentCoin
            val duration = 2000L
            val interval = 20L
            val steps = maxOf(1, (duration / interval).toInt())
            val step = (end - start) / steps
            var currentStep = 0

            while (animatedBalance < end && currentStep < steps) {
                kotlinx.coroutines.delay(interval)
                animatedBalance += step
                if (animatedBalance > end) animatedBalance = end
                currentStep++
            }
            animatedBalance = end
        }
    }

    BackHandler { onClose(false) }

    Dialog(
        onDismissRequest = { onClose(false) },
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.7f))
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) { onClose(false) }
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center)
            ) {
                    CommonLottieView(
                        animationName = "CoinArrivedWithProgressDialog",
                        loop = false,
                        modifier = Modifier.size(lottieWidth, lottieHeight),
                        onProgressChange = { progress = it }
                    )

                    val contentAlpha by animateFloatAsState(
                        targetValue = if (progress < 0.19f) 0f else if (progress < 0.38f) ((progress - 0.19f) / 0.19f) else 1f,
                        animationSpec = tween(0),
                        label = "contentAlpha"
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = (158 * scaleY).dp)
                            .alpha(contentAlpha),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Congrats!",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            fontStyle = FontStyle.Italic,
                            color = Color.Black,
                            modifier = Modifier
                                .width((256 * scaleX).dp)
                                .padding(start = (34 * scaleX).dp, top = (18 * scaleY).dp)
                        )

                        Text(
                            text = desc,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Normal,
                            color = Color.Black,
                            modifier = Modifier.padding(top = (33 * scaleY).dp)
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(top = (12 * scaleY).dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.img_coin_arrived_dialog_coin),
                                contentDescription = null,
                                modifier = Modifier.size((60 * scaleX).dp, (60 * scaleY).dp),
                                contentScale = ContentScale.FillBounds
                            )
                            Text(
                                text = NumberFormat.getIntegerInstance().format(coinNum),
                                fontSize = 42.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black,
                                modifier = Modifier.padding(start = 4.dp)
                            )
                        }

                        Box(
                            modifier = Modifier
                                .padding(top = (12 * scaleY).dp)
                                .width((215 * scaleX).dp)
                                .height((36 * scaleY).dp)
                        ) {
                            // Progress bar background
                            Box(
                                modifier = Modifier
                                    .width((215 * scaleX).dp)
                                    .height((12 * scaleY).dp)
                                    .clip(RoundedCornerShape((6 * scaleY).dp))
                                    .background(Color.Black.copy(alpha = 0.1f))
                                    .align(Alignment.CenterStart)
                            )
                            // Progress bar fill
                            Box(
                                modifier = Modifier
                                    .width((215 * scaleX * percentage).dp)
                                    .height((12 * scaleY).dp)
                                    .clip(RoundedCornerShape((6 * scaleY).dp))
                                    .background(
                                        Brush.horizontalGradient(
                                            listOf(Color(0xFFF56B96), Color(0xFFF7E840))
                                        )
                                    )
                                    .align(Alignment.CenterStart)
                            )
                            // Target cash icon
                            Box(
                                modifier = Modifier
                                    .align(Alignment.CenterEnd)
                                    .offset(x = (9 * scaleX).dp, y = (-1 * scaleY).dp)
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.img_conversion_rules_cash),
                                    contentDescription = null,
                                    modifier = Modifier.size((46 * scaleX).dp, (46 * scaleY).dp)
                                )
                                Text(
                                    text = "$${targetCash.toInt()}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.Black.copy(alpha = 0.7f),
                                    modifier = Modifier
                                        .align(Alignment.BottomCenter)
                                        .offset(y = (13 * scaleY).dp)
                                )
                            }
                            // Current progress icon
                            if (percentage <= 0.82f) {
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.CenterStart)
                                        .offset(
                                            x = (-6 * scaleX + (215 - 36 + 12) * scaleX * percentage).dp,
                                            y = (4 * scaleY).dp
                                        )
                                ) {
                                    Image(
                                        painter = painterResource(id = R.drawable.img_running_pick_coin_stack),
                                        contentDescription = null,
                                        modifier = Modifier.size((36 * scaleX).dp, (36 * scaleY).dp)
                                    )
                                    Text(
                                        text = String.format(Locale.US, "%.1f%%", percentage * 100),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color.Black.copy(alpha = 0.7f),
                                        modifier = Modifier
                                            .align(Alignment.BottomCenter)
                                            .offset(y = (13 * scaleY).dp)
                                    )
                                }
                            }
                        }

                        Box(
                            modifier = Modifier.padding(top = (62 * scaleY).dp)
                        ) {
                            if (showAdButton) {
                                Box(
                                    modifier = Modifier
                                        .width((230 * scaleX).dp)
                                        .height((56 * scaleY).dp)
                                        .clip(RoundedCornerShape(28.dp))
                                        .background(Color.Black)
                                        .clickable { onAdButtonTap() },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Image(
                                            painter = painterResource(id = R.drawable.ic_exchange_ad_play),
                                            contentDescription = null,
                                            modifier = Modifier.size((33 * scaleX).dp, (26 * scaleY).dp)
                                        )
                                        Spacer(modifier = Modifier.width((2 * scaleX).dp))
                                        Text(
                                            text = "Double Claim",
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Black,
                                            fontStyle = FontStyle.Italic,
                                            color = Color.White
                                        )
                                    }
                                }
                            } else {
                                Box(
                                    modifier = Modifier
                                        .width((230 * scaleX).dp)
                                        .height((56 * scaleY).dp)
                                        .clip(RoundedCornerShape(28.dp))
                                        .background(Color.Black)
                                        .clickable { onClose(true) },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = getButtonText,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Black,
                                        fontStyle = FontStyle.Italic,
                                        color = Color.White
                                    )
                                }
                            }

                            if (showFingerGuide) {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_finger_guide),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size((113 * scaleX).dp, (99 * scaleX).dp)
                                        .align(Alignment.BottomEnd)
                                        .offset(x = (25 * scaleX).dp, y = (75 * scaleY).dp)
                                )
                            }
                        }

                        Image(
                            painter = painterResource(id = R.drawable.ic_coin_arrived_dialog_close),
                            contentDescription = "Close",
                            modifier = Modifier
                                .padding(top = (47 * scaleY).dp)
                                .size((30 * scaleX).dp, (30 * scaleY).dp)
                                .clickable(
                                    indication = null,
                                    interactionSource = remember { MutableInteractionSource() }
                                ) { onClose(false) }
                        )
                    }
                }

                if (showBalance) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .statusBarsPadding()
                            .padding(top = 7.dp)
                    ) {
                        CommonLottieView(
                            animationName = "AddCoinEffect",
                            loop = false,
                            modifier = Modifier
                                .size((143 * scaleX).dp, (134 * scaleY).dp)
                                .offset(y = (-88 * scaleY).dp)
                        )
                        Text(
                            text = NumberFormat.getIntegerInstance().format(animatedBalance.toInt()),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            fontStyle = FontStyle.Italic,
                            color = Color.Black,
                            modifier = Modifier
                                .padding(start = (50 * scaleX).dp, top = 20.dp) // Adjust padding as needed
                        )
                    }
                }
            }
        }
    }
