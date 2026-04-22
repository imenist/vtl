package com.vitalo.markrun.ui.lesson

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.vitalo.markrun.data.remote.model.Training

@Composable
fun LessonCardView(
    training: Training,
    partIndex: Int,
    onClick: () -> Unit
) {
    val cardWidth = 236.dp
    val cardHeight = 156.dp

    Box(
        modifier = Modifier
            .width(cardWidth)
            .height(cardHeight)
            .clip(RoundedCornerShape(20.dp))
            .clickable { onClick() }
    ) {
        AsyncImage(
            model = training.cover,
            contentDescription = training.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.5f),
                            Color.Transparent
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {
            Text(
                text = "Part ${partIndex + 1}:\n${training.name ?: ""}",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.weight(1f))

            ConsumeAndDurationView(training = training)
        }
    }
}

@Composable
fun ConsumeAndDurationView(training: Training) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = "${training.calorie ?: 0} kcal",
            fontSize = 11.sp,
            color = Color(0xFFFFCA7D)
        )

        Spacer(modifier = Modifier.width(6.dp))

        Box(
            modifier = Modifier
                .width(2.dp)
                .height(4.dp)
                .clip(RoundedCornerShape(1.dp))
                .background(Color(0xFFD6D6D6))
        )

        Spacer(modifier = Modifier.width(6.dp))

        Text(
            text = "${(training.duration ?: 0) / 60} min",
            fontSize = 11.sp,
            color = Color(0xFFD6D6D6)
        )
    }
}
