package com.vitalo.markrun.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.vitalo.markrun.R
import com.vitalo.markrun.ui.theme.DarkBackground
import com.vitalo.markrun.ui.theme.GradientGreenEnd
import com.vitalo.markrun.ui.theme.GradientGreenStart

@Composable
fun CustomTabBar(
    selectedTab: Int,
    onTabTapped: (Int) -> Unit
) {
    val bottomPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        // Glow effect behind the dark bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(78.dp)
                .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(GradientGreenEnd, GradientGreenStart)
                    ),
                    alpha = 0.4f
                )
                .align(Alignment.BottomCenter)
        )

        // Dark background bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(78.dp + bottomPadding)
                .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                .background(DarkBackground)
                .align(Alignment.BottomCenter)
        )

        // Tab items row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = bottomPadding)
                .height(78.dp)
                .align(Alignment.BottomCenter),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Top
        ) {
            // Tab 0 - Training
            TabItem(
                iconRes = R.drawable.ic_home_tab_training,
                selected = selectedTab == 0,
                onClick = { onTabTapped(0) }
            )

            // Tab 1 - Task
            TabItem(
                iconRes = R.drawable.ic_home_tab_task,
                selected = selectedTab == 1,
                onClick = { onTabTapped(1) }
            )

            // Tab 2 - Running (center raised button)
            Box(
                modifier = Modifier
                    .offset(y = (-30).dp)
                    .size(60.dp)
                    .shadow(
                        elevation = 8.dp,
                        shape = CircleShape,
                        ambientColor = Color(0xFFEDF23D).copy(alpha = 0.5f),
                        spotColor = Color(0xFFEDF23D).copy(alpha = 0.5f)
                    )
                    .clip(CircleShape)
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(GradientGreenEnd, GradientGreenStart)
                        )
                    )
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) { onTabTapped(2) },
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_home_tab_running),
                    contentDescription = "Running",
                    modifier = Modifier.size(36.dp)
                )
            }

            // Tab 3 - Collection
            TabItem(
                iconRes = R.drawable.ic_home_tab_collection,
                selected = selectedTab == 3,
                onClick = { onTabTapped(3) }
            )

            // Tab 4 - Exchange
            TabItem(
                iconRes = R.drawable.ic_home_tab_exchange,
                selected = selectedTab == 4,
                onClick = { onTabTapped(4) }
            )
        }
    }
}

@Composable
private fun TabItem(
    iconRes: Int,
    selected: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .width(67.dp)
            .height(70.dp)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        if (selected) {
            // Indicator inverted triangle
            androidx.compose.foundation.Canvas(
                modifier = Modifier
                    .width(35.dp)
                    .height(9.dp)
            ) {
                val path = androidx.compose.ui.graphics.Path().apply {
                    moveTo(0f, 0f)
                    lineTo(size.width, 0f)
                    lineTo(size.width / 2f, size.height)
                    close()
                }
                drawPath(
                    path = path,
                    brush = Brush.horizontalGradient(
                        colors = listOf(GradientGreenEnd, GradientGreenStart)
                    )
                )
            }
            Spacer(modifier = Modifier.height(1.dp))
        } else {
            Spacer(modifier = Modifier.height(10.dp))
        }

        Image(
            painter = painterResource(iconRes),
            contentDescription = null,
            modifier = Modifier.size(30.dp),
            alpha = if (selected) 1f else 0.4f
        )
    }
}
