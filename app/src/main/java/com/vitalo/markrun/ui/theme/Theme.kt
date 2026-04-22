package com.vitalo.markrun.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFFC9FF6B),
    onPrimary = Color(0xFF0D120E),
    secondary = Color(0xFFFFED29),
    onSecondary = Color(0xFF0D120E),
    background = Color(0xFFFFFFFF),
    onBackground = Color(0xFF0D120E),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF0D120E),
)

@Composable
fun VitaloTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            content()
        }
    }
}
