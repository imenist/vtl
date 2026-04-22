package com.vitalo.markrun.ui.lesson

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.vitalo.markrun.R

@Composable
fun EarnRulesDialog(onClose: () -> Unit) {
    Dialog(
        onDismissRequest = onClose,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.7f))
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) { onClose() },
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .width(335.dp)
                    .height(340.dp)
                    .clip(RoundedCornerShape(30.dp))
                    .background(Color.White)
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) {}
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_close_2),
                        contentDescription = "Close",
                        modifier = Modifier
                            .padding(start = 20.dp)
                            .size(24.dp)
                            .align(Alignment.CenterStart)
                            .clickable { onClose() }
                    )

                    Text(
                        text = "RULES",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.Black,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(top = 10.dp)
                    )
                }

                Column(
                    modifier = Modifier
                        .padding(top = 20.dp, start = 30.dp, end = 30.dp, bottom = 30.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    RuleSection(
                        title = "1. Earn points by walking",
                        items = listOf(
                            buildAnnotatedString {
                                withStyle(SpanStyle(fontWeight = FontWeight.Black, color = Color.Black)) { append("• ") }
                                withStyle(SpanStyle(color = Color(0xFFFF6645))) { append("10 steps = 1 CaloCoin") }
                            },
                            buildAnnotatedString {
                                withStyle(SpanStyle(fontWeight = FontWeight.Black, color = Color.Black)) { append("• ") }
                                withStyle(SpanStyle(color = Color.Black)) { append("Daily step count resets at 12:00") }
                            }
                        )
                    )

                    Spacer(modifier = Modifier.height(7.dp))

                    RuleSection(
                        title = "2. Redeem points using steps",
                        items = listOf(
                            buildAnnotatedString {
                                withStyle(SpanStyle(fontWeight = FontWeight.Black, color = Color.Black)) { append("• ") }
                                withStyle(SpanStyle(color = Color(0xFFFF6645))) { append("≤ 1,000 CaloCoins") }
                                withStyle(SpanStyle(color = Color.Black)) { append(": Redeem directly without watching ads") }
                            },
                            buildAnnotatedString {
                                withStyle(SpanStyle(fontWeight = FontWeight.Black, color = Color.Black)) { append("• ") }
                                withStyle(SpanStyle(color = Color(0xFFFF6645))) { append("> 1,000 CaloCoins") }
                                withStyle(SpanStyle(color = Color.Black)) { append(": Watch ads to redeem;") }
                            },
                            buildAnnotatedString {
                                withStyle(SpanStyle(color = Color.Black)) { append("Check the app regularly to unlock ad-free rewards") }
                            }
                        )
                    )

                    Spacer(modifier = Modifier.height(7.dp))

                    RuleSection(
                        title = "3. Earn More CaloCoins",
                        items = listOf(
                            buildAnnotatedString {
                                withStyle(SpanStyle(fontWeight = FontWeight.Black, color = Color.Black)) { append("• ") }
                                withStyle(SpanStyle(color = Color.Black)) { append("More activities to earn CaloCoins! Such as daily sign-in, golden eggs, slots, etc.") }
                            },
                            buildAnnotatedString {
                                withStyle(SpanStyle(fontWeight = FontWeight.Black, color = Color.Black)) { append("• ") }
                                withStyle(SpanStyle(color = Color.Black)) { append("Other ways to earn CaloCoins include exercising or running; 1 kcal = 1 CaloCoin.") }
                            }
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun RuleSection(
    title: String,
    items: List<androidx.compose.ui.text.AnnotatedString>
) {
    Text(
        text = title,
        fontSize = 12.sp,
        fontWeight = FontWeight.SemiBold,
        color = Color.Black
    )
    Spacer(modifier = Modifier.height(7.dp))
    items.forEach { annotatedText ->
        Text(
            text = annotatedText,
            fontSize = 12.sp,
            lineHeight = 22.sp
        )
    }
}
