package com.vitalo.markrun.ui.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun DotIndicator(
    total: Int,
    selectedIndex: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        repeat(total) { index ->
            Box(
                modifier = Modifier
                    .size(if (index == selectedIndex) 10.dp else 8.dp)
                    .clip(CircleShape)
                    .background(
                        if (index == selectedIndex) Color.Black else Color(0xFFD6D6D6)
                    )
            )
        }
    }
}
