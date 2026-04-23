package com.vitalo.markrun.ui.home

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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
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
import com.vitalo.markrun.R
import kotlinx.coroutines.delay

@Composable
fun AdEarnCoinFloatingButton(
    modifier: Modifier = Modifier,
    coinNum: Int,
    isLoading: Boolean,
    isCooling: Boolean,
    cooldownSeconds: Int,
    cooldownTotalSeconds: Int,
    onTap: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition()
    val bounceOffset by infiniteTransition.animateFloat(
        initialValue = -10f,
        targetValue = 10f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bounce"
    )

    Box(
        modifier = modifier
            .offset(y = bounceOffset.dp)
            .clickable(
                enabled = !isLoading,
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) { onTap() },
    ) {
        // iOS 对应 ic_lesson_ad_entrance_coin
        Image(
            painter = painterResource(id = R.drawable.ic_lesson_ad_entrance_coin),
            contentDescription = null,
            modifier = Modifier
                .size(70.dp)
                // 向上偏移，使得下面的文案框能够有一点点重叠，iOS 也是在这个 Image 底部减去了 3.9.UI 的 padding
                .offset(y = 4.dp),
            contentScale = ContentScale.Fit
        )

        val shape = RoundedCornerShape(15.dp)
        val colorful = Brush.horizontalGradient(
            listOf(Color(0xFFFD4D82), Color(0xFFFF8F45), Color(0xFFF8D229))
        )
        val disabled = Brush.horizontalGradient(
            listOf(Color(0xFFBFAF8C), Color(0xFF9E9E9E))
        )

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                // 使用 zIndex 保证其显示在图片上面
                .size(width = 50.dp, height = 21.dp)
                .clip(shape)
                .background(if (isCooling) disabled else colorful),
            contentAlignment = Alignment.Center
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator(
                        strokeWidth = 2.dp,
                        color = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                }
                isCooling -> {
                    val safeTotal = cooldownTotalSeconds.coerceAtLeast(1)
                    val safeLeft = cooldownSeconds.coerceIn(0, safeTotal)
                    val mm = (safeLeft / 60).toString().padStart(2, '0')
                    val ss = (safeLeft % 60).toString().padStart(2, '0')
                    Text(
                        text = "$mm:$ss",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        fontStyle = FontStyle.Italic,
                        color = Color.White
                    )
                }
                else -> {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_exchange_ad_play),
                            contentDescription = null,
                            modifier = Modifier.size(10.dp),
                            contentScale = ContentScale.Fit
                        )
                        Text(
                            text = "$coinNum",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            fontStyle = FontStyle.Italic,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}
