package com.vitalo.markrun.ui.common

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.vitalo.markrun.R
import java.text.NumberFormat

@Composable
fun CoinBalanceView(
    showExchange: Boolean = false,
    coinExchangeRate: Double = 0.0,
    onTapExchange: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    viewModel: CoinBalanceViewModel = hiltViewModel()
) {
    val totalCoin by viewModel.coinBalance.collectAsState()

    val cornerShape = RoundedCornerShape(topEnd = 24.dp, bottomEnd = 24.dp)

    Row(
        modifier = modifier
            .height(48.dp)
            .clip(cornerShape)
            .background(
                Brush.horizontalGradient(
                    colorStops = arrayOf(
                        0.0f to Color(0xFF2E824F),
                        0.82f to Color(0xFFA3C93B),
                        1.0f to Color(0xFFA1C21C)
                    )
                )
            )
            .border(0.5.dp, Color.White.copy(alpha = 0.4f), cornerShape),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(modifier = Modifier.width(1.dp))

        if (coinExchangeRate > 0) {
            Spacer(modifier = Modifier.width(6.dp))

            androidx.compose.foundation.layout.Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_coin_balance),
                        contentDescription = null,
                        modifier = Modifier.size(17.dp),
                        contentScale = ContentScale.Fit
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = NumberFormat.getIntegerInstance().format(totalCoin.toInt()),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                Text(
                    text = "≈ ${formatCurrency(totalCoin / coinExchangeRate)}",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFFEE42)
                )
            }
        } else {
            Spacer(modifier = Modifier.width(8.dp))

            Image(
                painter = painterResource(id = R.drawable.ic_coin_balance),
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.width(4.dp))

            Text(
                text = NumberFormat.getIntegerInstance().format(totalCoin.toInt()),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                fontStyle = FontStyle.Italic,
                color = Color.White
            )
        }

        if (showExchange) {
            Spacer(modifier = Modifier.width(8.dp))
            RedeemButton(onClick = { onTapExchange?.invoke() })
            Spacer(modifier = Modifier.width(8.dp))
        } else {
            Spacer(modifier = Modifier.width(25.dp))
        }
    }
}

@Composable
private fun RedeemButton(onClick: () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition(label = "redeem_pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0.94f,
        animationSpec = infiniteRepeatable(
            animation = tween(600),
            repeatMode = RepeatMode.Reverse
        ),
        label = "redeem_scale"
    )

    Box(
        modifier = Modifier
            .scale(scale)
            .height(34.dp)
            .clip(RoundedCornerShape(17.dp))
            .background(
                Brush.horizontalGradient(
                    colors = listOf(
                        Color(0xFFFFEF3E),
                        Color(0xFFF7FFBB),
                        Color(0xFFCDFF76)
                    )
                )
            )
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) { onClick() }
            .padding(horizontal = 13.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Redeem",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            fontStyle = FontStyle.Italic,
            color = Color(0xFF324B1A)
        )
    }
}

private fun formatCurrency(value: Double): String {
    return if (value % 1.0 == 0.0) {
        "$${value.toInt()}"
    } else {
        String.format("$%.2f", value)
    }
}
