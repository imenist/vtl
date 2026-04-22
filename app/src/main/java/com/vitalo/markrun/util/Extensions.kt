package com.vitalo.markrun.util

import androidx.compose.ui.graphics.Color

fun Color.Companion.fromHex(hex: String): Color {
    val cleanHex = hex.trimStart('#')
    return when (cleanHex.length) {
        6 -> Color(("FF$cleanHex").toLong(16))
        8 -> Color(cleanHex.toLong(16))
        else -> Color.Black
    }
}
