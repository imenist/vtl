package com.vitalo.markrun.ui.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vitalo.markrun.ui.theme.DarkBackground

@Composable
fun CommonErrorView(
    onRetry: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Network Error",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.Black.copy(alpha = 0.5f)
        )

        if (onRetry != null) {
            Surface(
                modifier = Modifier
                    .padding(top = 29.dp)
                    .width(170.dp)
                    .clickable { onRetry() },
                shape = RoundedCornerShape(50.dp),
                color = DarkBackground
            ) {
                Text(
                    text = "RETRY",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    fontStyle = FontStyle.Italic,
                    color = Color.White,
                    modifier = Modifier.padding(vertical = 17.dp, horizontal = 15.dp)
                )
            }
        }
    }
}
