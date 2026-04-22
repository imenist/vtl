package com.vitalo.markrun.ui.settings

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun FeedbackDialog(onClose: () -> Unit) {
    var feedbackText by remember { mutableStateOf("") }
    var emailText by remember { mutableStateOf("") }
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.7f))
            .clickable { onClose() },
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(Color.White)
                .clickable(enabled = false) {}
                .padding(24.dp)
        ) {
            Text(
                text = "Feedback",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0D120E)
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = feedbackText,
                onValueChange = { feedbackText = it },
                placeholder = { Text("Tell us what you think...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                maxLines = 5,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFCDDD44),
                    unfocusedBorderColor = Color(0xFFE0E0E0)
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = emailText,
                onValueChange = { emailText = it },
                placeholder = { Text("Your email (optional)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFCDDD44),
                    unfocusedBorderColor = Color(0xFFE0E0E0)
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onClose) {
                    Text(
                        text = "Cancel",
                        color = Color(0xFF0D120E).copy(alpha = 0.5f)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = {
                        val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                            data = Uri.parse("mailto:feedback@mark-run.com")
                            putExtra(Intent.EXTRA_SUBJECT, "MarkRun Feedback")
                            putExtra(
                                Intent.EXTRA_TEXT,
                                "$feedbackText\n\nFrom: $emailText"
                            )
                        }
                        try {
                            context.startActivity(emailIntent)
                        } catch (_: Exception) {
                        }
                        onClose()
                    },
                    enabled = feedbackText.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF0D120E)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Submit", color = Color.White)
                }
            }
        }
    }
}
