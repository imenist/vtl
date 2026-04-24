package com.vitalo.markrun.ui.exchange

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vitalo.markrun.R

@Composable
fun ConversionRulesDialog(
    coinExchangeRate: Double,
    onClose: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.7f))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClose() },
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.width(335.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(15.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(30.dp))
                    .background(Color.White)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { /* Consume click to prevent dismiss */ },
                contentAlignment = Alignment.TopCenter
            ) {
                // Blur top effect
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(45.dp)
                        .blur(10.dp)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    Color.White,
                                    Color(0xFFF66B96),
                                    Color(0xFFF8E840)
                                )
                            )
                        )
                )

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Conversion Rules",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        fontStyle = FontStyle.Italic,
                        color = Color(0xFF0D120E),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 11.dp)
                    )

                    Image(
                        painter = painterResource(id = R.drawable.img_conversion_rules_cash),
                        contentDescription = null,
                        modifier = Modifier.size(150.dp)
                    )

                    Row(
                        modifier = Modifier
                            .padding(start = 20.dp, end = 20.dp, bottom = 20.dp)
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(30.dp))
                            .background(Color(0xFFF5F2ED))
                            .padding(top = 14.dp, bottom = 20.dp, start = 20.dp, end = 20.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(7.dp)
                        ) {
                            Text(
                                text = "CaloCoin",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF0D120E).copy(alpha = 0.5f),
                                modifier = Modifier.height(22.dp)
                            )
                            Text(
                                text = "${coinExchangeRate.toInt()}",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0D120E)
                            )
                        }

                        Text(
                            text = "=",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            modifier = Modifier.padding(top = 22.dp)
                        )

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(7.dp)
                        ) {
                            Text(
                                text = "USD",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF0D120E).copy(alpha = 0.5f),
                                modifier = Modifier.height(22.dp)
                            )
                            Text(
                                text = "$1",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0D120E)
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(28.dp))
                }
            }

            Image(
                painter = painterResource(id = R.drawable.ic_coin_arrived_dialog_close),
                contentDescription = "Close",
                modifier = Modifier
                    .size(60.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { onClose() }
            )
        }
    }
}
