package com.vitalo.markrun.ui.running

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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

@Composable
fun DeleteRecordDialog(
    onClose: (Boolean) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.7f)),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .widthIn(max = 280.dp)
                .clip(RoundedCornerShape(30.dp))
                .background(Color.White)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(23.dp))

                Image(
                    painter = painterResource(id = R.drawable.img_delete_warning),
                    contentDescription = null,
                    modifier = Modifier.size(138.dp),
                    contentScale = ContentScale.Fit
                )

                Spacer(modifier = Modifier.height(22.dp))

                Text(
                    text = "Tips",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black,
                    fontStyle = FontStyle.Italic,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Are you sure you want to delete this record?",
                    fontSize = 12.sp,
                    color = Color.Black,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.width(216.dp)
                )

                Spacer(modifier = Modifier.height(25.dp))

                Box(
                    modifier = Modifier
                        .width(170.dp)
                        .clip(RoundedCornerShape(50.dp))
                        .background(Color(0xFFC00F0C))
                        .clickable { onClose(true) }
                        .padding(vertical = 17.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Delete",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        fontStyle = FontStyle.Italic,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Cancel",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF757575),
                    modifier = Modifier
                        .clickable { onClose(false) }
                        .padding(vertical = 13.dp)
                )
            }

            IconButton(
                onClick = { onClose(false) },
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
