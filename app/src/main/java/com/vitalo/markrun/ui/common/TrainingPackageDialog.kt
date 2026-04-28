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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vitalo.markrun.R
import java.text.NumberFormat

@Composable
fun TrainingPackageDialog(
    coinNum: Int,
    titleText: String = "训练礼包",
    claimButtonText: String = "领取",
    showAdIcon: Boolean = true,
    onTapClaim: () -> Unit = {},
    onTapClose: () -> Unit = {}
) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val lottieWidth = screenWidth
    val lottieHeight = lottieWidth / (375f / 812f)
    val scaleX = (screenWidth.value / 375f)
    val scaleY = (lottieHeight.value / 812f)

    val coinExchangeRate = 1000.0 // Hardcoded for now, similar to other places

    var timeElapsed by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(Unit) {
        val duration = 1200L
        val interval = 20L
        val steps = (duration / interval).toInt()
        val stepAmount = 1.2f / steps
        for (i in 0..steps) {
            kotlinx.coroutines.delay(interval)
            timeElapsed += stepAmount
        }
    }

    val coinViewOpacity = if (timeElapsed > 0f) minOf(1f, timeElapsed / 0.46f) else 0f
    val coinViewScale = if (timeElapsed > 0f) minOf(1f, 0.03f + 0.97f * (timeElapsed / 1.0f)) else 0.03f
    val titleOpacity = if (timeElapsed > 0.86f) minOf(1f, (timeElapsed - 0.86f) / 0.34f) else 0f
    val claimButtonOpacity = if (timeElapsed > 0.86f) minOf(1f, (timeElapsed - 0.86f) / 0.34f) else 0f

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.7f))
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {}
    ) {
        Box(
            modifier = Modifier
                .width(lottieWidth)
                .height(lottieHeight)
                .align(Alignment.TopCenter)
        ) {
            CommonLottieView(
                animationName = "TrainingPackage",
                loop = false,
                modifier = Modifier.size(lottieWidth, lottieHeight)
            )

            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(bottom = (40 * scaleY).dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = titleText,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF0D120D),
                    modifier = Modifier
//                        .padding(top = (15 * scaleY).dp)
                        .alpha(titleOpacity)
                )

                Spacer(Modifier.height(40.dp))

                Box(
                    modifier = Modifier
//                        .padding(top = (15 * scaleY).dp)
                        .width((216 * scaleX).dp)
                        .height((140 * scaleY).dp)
                        .scale(coinViewScale)
                        .alpha(coinViewOpacity)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.White)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.img_coin_arrived_dialog_coin),
                                contentDescription = null,
                                modifier = Modifier
                                    .size((60 * scaleX).dp, (60 * scaleY).dp)
                                    .offset(x = (5 * scaleX).dp)
                            )
                            Text(
                                text = NumberFormat.getIntegerInstance().format(coinNum),
                                fontSize = 42.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1F1F1F),
                                maxLines = 1,
                                textAlign = TextAlign.Start,
                                modifier = Modifier
                                    .width((108 * scaleX).dp)
                                    .offset(x = (-5 * scaleX).dp)
                            )
                        }

                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.padding(top = (5 * scaleY).dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.img_training_package_exchange_bg),
                                contentDescription = null,
                                modifier = Modifier.size((155 * scaleX).dp, (56 * scaleY).dp)
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center,
                                modifier = Modifier.padding(top = (15 * scaleY).dp)
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.width((67 * scaleX).dp)
                                ) {
                                    Text(
                                        text = NumberFormat.getIntegerInstance().format(coinNum),
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color.Black,
                                        maxLines = 1
                                    )
                                    Text(
                                        text = "CaloCoins",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color(0xFF0D120D)
                                    )
                                }

                                Text(
                                    text = "≈",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black.copy(alpha = 0.5f),
                                    modifier = Modifier.padding(horizontal = (3 * scaleX).dp)
                                )

                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.width((67 * scaleX).dp)
                                ) {
                                    Text(
                                        text = String.format("$%.2f", coinNum / coinExchangeRate),
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFFF9400),
                                        maxLines = 1
                                    )
                                    Text(
                                        text = "USD",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color(0xFF0D120D)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(Modifier.height(30.dp))

                Box(
                    modifier = Modifier
//                        .padding(top = (10 * scaleY).dp)
                        .width((230 * scaleX).dp)
                        .height((46 * scaleY).dp)
                        .alpha(claimButtonOpacity)
                        .clip(RoundedCornerShape(28.dp))
                        .background(Color.Black)
                        .clickable { onTapClaim() },
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (showAdIcon) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_exchange_ad_play),
                                contentDescription = null,
                                modifier = Modifier.size((17 * scaleX).dp, (14 * scaleY).dp)
                            )
                            Spacer(modifier = Modifier.width((6 * scaleX).dp))
                        }
                        Text(
                            text = claimButtonText,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            fontStyle = FontStyle.Italic,
                            color = Color.White
                        )
                    }
                }
            }
        }

        Image(
            painter = painterResource(id = R.drawable.ic_coin_arrived_dialog_close),
            contentDescription = "Close",
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = (59 * scaleY).dp, end = (20 * scaleX).dp)
                .size((30 * scaleX).dp, (30 * scaleY).dp)
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) { onTapClose() }
        )
    }
}
