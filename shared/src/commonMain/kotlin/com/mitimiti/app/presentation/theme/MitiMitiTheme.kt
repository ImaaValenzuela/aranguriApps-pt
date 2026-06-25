package com.mitimiti.app.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme =
    lightColorScheme(
        primary = Color(0xFF4CAF50),
        onPrimary = Color.White,
        primaryContainer = Color(0xFFC8E6C9),
        onPrimaryContainer = Color(0xFF1B5E20),
        secondary = Color(0xFF81C784),
        onSecondary = Color.Black,
        background = Color(0xFFF9FBF9),
        onBackground = Color(0xFF1B1C1B),
        surface = Color.White,
        onSurface = Color(0xFF1B1C1B),
    )

private val DarkColorScheme =
    darkColorScheme(
        primary = Color(0xFF81C784),
        onPrimary = Color(0xFF1B5E20),
        primaryContainer = Color(0xFF2E7D32),
        onPrimaryContainer = Color(0xFFC8E6C9),
        secondary = Color(0xFFA5D6A7),
        onSecondary = Color.Black,
        background = Color(0xFF121412),
        onBackground = Color(0xFFE2E3E2),
        surface = Color(0xFF1A1C1A),
        onSurface = Color(0xFFE2E3E2),
    )

@Composable
@Suppress("FunctionNaming")
fun MitiMitiTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        content = content,
    )
}
