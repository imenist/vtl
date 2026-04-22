package com.vitalo.markrun.ui.permission

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vitalo.markrun.R
import com.vitalo.markrun.ui.theme.DarkBackground

@Composable
fun LocationPermissionDeniedDialog(
    onGoToSettings: () -> Unit,
    onCancel: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.7f))
            .clickable { onCancel() },
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .width(256.dp)
                .clip(RoundedCornerShape(30.dp))
                .background(Color.White)
                .clickable(enabled = false) {}
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(23.dp))

                Image(
                    painter = painterResource(id = R.drawable.img_location_permission),
                    contentDescription = null,
                    modifier = Modifier.size(138.dp),
                    contentScale = ContentScale.Fit
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Location Access Required",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black,
                    fontStyle = FontStyle.Italic,
                    color = Color.Black,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "We need to obtain your location information to record movement tracks and generate movement routes.\n\nWithout authorized location permissions, the map track recording function cannot be used normally.",
                    fontSize = 12.sp,
                    color = Color.Black,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )

                Spacer(modifier = Modifier.height(15.dp))

                Button(
                    onClick = onGoToSettings,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .padding(horizontal = 28.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = DarkBackground)
                ) {
                    Text(
                        text = "Go to Settings",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        fontStyle = FontStyle.Italic,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(28.dp))
            }

            IconButton(
                onClick = onCancel,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(4.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_close),
                    contentDescription = "Close",
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}
