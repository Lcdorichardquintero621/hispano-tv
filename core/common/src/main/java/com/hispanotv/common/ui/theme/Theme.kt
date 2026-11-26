package com.hispanotv.common.ui.theme

import android.content.Context
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

@Composable
fun HispanoTVTheme(
    darkTheme: Boolean = true, // Force dark for TV
    content: @Composable () -> Unit
) {
    val colorScheme = DarkColorScheme
    val context = LocalContext.current

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}

// Helper to get theme colors
@Composable
fun <T> withThemeColors(crossinline block: (ColorScheme) -> T): T {
    return block(MaterialTheme.colorScheme)
}