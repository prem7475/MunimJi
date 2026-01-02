package com.example.munimji.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = NavyPrimary,
    secondary = NavySecondary,
    tertiary = GoldAccent
)

private val LightColorScheme = lightColorScheme(
    primary = NavyPrimary,
    secondary = NavySecondary,
    tertiary = GoldAccent
)

@Composable
fun MunimJiTheme(
    // Force light theme for professional ledger look
    darkTheme: Boolean = false,
    // Disable dynamic color for consistent Teal theme
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
