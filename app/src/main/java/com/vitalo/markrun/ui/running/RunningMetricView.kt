package com.vitalo.markrun.ui.running

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vitalo.markrun.ui.theme.TextPrimary

@Composable
fun RunningMetricView(
    value: String,
    unit: String,
    valueFontSize: Float = 22f,
    unitFontSize: Float = 12f
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            fontSize = valueFontSize.sp,
            fontWeight = FontWeight.Medium,
            color = TextPrimary
        )
        Text(
            text = unit,
            fontSize = unitFontSize.sp,
            color = TextPrimary,
            modifier = Modifier.height(22.dp)
        )
    }
}
