package com.vitalo.markrun.ui.running

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vitalo.markrun.R

@Composable
fun RunningPauseOverlay(
    onStop: () -> Unit,
    onContinue: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.7f)),
        contentAlignment = Alignment.Center
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(36.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .width(100.dp)
                    .clickable { onStop() }
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_running_stop),
                    contentDescription = "Stop",
                    modifier = Modifier.size(70.dp)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Finish",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .width(100.dp)
                    .clickable { onContinue() }
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_running_continue),
                    contentDescription = "Continue",
                    modifier = Modifier.size(70.dp)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Continue",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
