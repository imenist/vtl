package com.vitalo.markrun.ui.mine

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vitalo.markrun.data.local.db.entity.WeightRecord
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun AddWeightDialog(
    onSave: (WeightRecord?) -> Unit
) {
    var weight by remember { mutableStateOf("") }
    var isLbs by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf(Date()) }

    val kgToLbsRatio = 2.20462262
    val minWeightKg = 20
    val maxWeightKg = 300

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.7f))
            .clickable { onSave(null) },
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .clip(RoundedCornerShape(30.dp))
                .background(Color.White)
                .clickable(enabled = false) {}
                .padding(24.dp)
        ) {
            // Date display
            val dateFormatter = remember { SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()) }
            Text(
                text = dateFormatter.format(selectedDate),
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF0D120E)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Weight input
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = weight,
                    onValueChange = { newValue ->
                        val filtered = newValue.filter { it.isDigit() || it == '.' }
                        if (filtered.length <= 6) {
                            weight = filtered
                        }
                    },
                    placeholder = {
                        Text("0.0", fontSize = 36.sp, color = Color.Black.copy(alpha = 0.3f))
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    textStyle = LocalTextStyle.current.copy(
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFDCCA9E),
                        unfocusedBorderColor = Color(0xFFD6D6D6)
                    )
                )

                Spacer(modifier = Modifier.width(12.dp))

                // Unit selector
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    UnitChip(
                        label = "KG",
                        selected = !isLbs,
                        onClick = {
                            if (isLbs) {
                                isLbs = false
                                val w = weight.toDoubleOrNull()
                                if (w != null) {
                                    weight = String.format("%.0f", w / kgToLbsRatio)
                                }
                            }
                        }
                    )
                    UnitChip(
                        label = "LB",
                        selected = isLbs,
                        onClick = {
                            if (!isLbs) {
                                isLbs = true
                                val w = weight.toDoubleOrNull()
                                if (w != null) {
                                    weight = String.format("%.0f", w * kgToLbsRatio)
                                }
                            }
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = { onSave(null) }) {
                    Text(
                        text = "Cancel",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF0D120E).copy(alpha = 0.5f)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                val weightValue = weight.toDoubleOrNull()
                val isValid = weightValue != null && run {
                    val kg = if (isLbs) weightValue / kgToLbsRatio else weightValue
                    kg >= minWeightKg && kg <= maxWeightKg
                }

                TextButton(
                    onClick = {
                        if (weightValue != null) {
                            val weightInKg = if (isLbs) {
                                (weightValue / kgToLbsRatio).toInt()
                            } else {
                                weightValue.toInt()
                            }
                            val record = WeightRecord(
                                id = UUID.randomUUID().toString(),
                                date = selectedDate.time,
                                weight = weightInKg
                            )
                            onSave(record)
                        }
                    },
                    enabled = isValid
                ) {
                    Text(
                        text = "Submit",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isValid) Color.Black else Color.Gray
                    )
                }
            }
        }
    }
}

@Composable
private fun UnitChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    if (selected) {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.White,
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(Color.Black)
                .clickable { onClick() }
                .padding(horizontal = 7.dp, vertical = 6.dp)
        )
    } else {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFFD6D6D6),
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(Color.Transparent)
                .clickable { onClick() }
                .padding(horizontal = 7.dp, vertical = 6.dp)
        )
    }
}
