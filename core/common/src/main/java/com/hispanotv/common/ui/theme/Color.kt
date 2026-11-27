package com.hispanotv.common.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Dark theme colors (TV-friendly)
val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFBB86FC),
    primaryContainer = Color(0xFF3700B3),
    secondary = Color(0xFF03DAC6),
    secondaryContainer = Color(0xFF018786),
    tertiary = Color(0xFFFF0187),
    tertiaryContainer = Color(0xFFB00020),
    background = Color(0xFF121212),
    surface = Color(0xFF1E1E1E),
    surfaceVariant = Color(0xFF2C2C2C),
    error = Color(0xFFCF6679),
    onPrimary = Color(0xFF000000),
    onSecondary = Color(0xFF000000),
    onTertiary = Color(0xFF000000),
    onBackground = Color(0xFFFFFFFF),
    onSurface = Color(0xFFFFFFFF),
    onSurfaceVariant = Color(0xFFE0E0E0),
    onError = Color(0xFF000000),
    outline = Color(0xFF767676),
    outlineVariant = Color(0xFF4A4A4A),
    shadow = Color(0xFF000000),
    scrim = Color(0xFF000000),
    inverseSurface = Color(0xFFE0E0E0),
    inverseOnSurface = Color(0xFF121212),
    inversePrimary = Color(0xFF6200EE)
)

@Composable
fun PrimarySurfaceContainer(
    content: @Composable () -> Unit
) {
    androidx.compose.material3.Surface(
        color = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
    ) {
        content()
    }
}