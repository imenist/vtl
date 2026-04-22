package com.vitalo.markrun.ui.lesson

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vitalo.markrun.data.remote.model.Subject
import com.vitalo.markrun.data.remote.model.Training
import com.vitalo.markrun.ui.theme.GradientGreenEnd
import com.vitalo.markrun.ui.theme.GradientGreenStart

@Composable
fun SubjectSectionView(
    subject: Subject,
    onTrainingTap: (Training, Int) -> Unit
) {
    Column {
        // Section header
        Row(
            modifier = Modifier
                .height(28.dp)
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(GradientGreenEnd, GradientGreenStart)
                        )
                    )
            )
            Spacer(modifier = Modifier.width(9.dp))
            Text(
                text = subject.name,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black
            )
        }

        Spacer(modifier = Modifier.height(5.dp))

        // Horizontal scrolling training cards
        val trainings = subject.training
        if (!trainings.isNullOrEmpty()) {
            Row(
                modifier = Modifier
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                trainings.forEachIndexed { index, training ->
                    LessonCardView(
                        training = training,
                        partIndex = index,
                        onClick = { onTrainingTap(training, index) }
                    )
                }
            }
        }
    }
}
