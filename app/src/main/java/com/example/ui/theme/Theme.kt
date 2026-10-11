package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = RudraCyanNeon,
    onPrimary = Color(0xFF002026),
    primaryContainer = RudraCyanDark,
    onPrimaryContainer = RudraCyanSoft,

    secondary = RudraVioletNeon,
    onSecondary = Color(0xFF1E0038),
    secondaryContainer = RudraVioletDark,
    onSecondaryContainer = RudraVioletLight,

    tertiary = RudraGold,
    onTertiary = Color(0xFF382800),
    tertiaryContainer = RudraGoldDark,
    onTertiaryContainer = RudraGoldLight,

    background = RudraNavyDarkest,
    onBackground = RudraTextPrimary,

    surface = RudraNavyCard,
    onSurface = RudraTextPrimary,
    surfaceVariant = RudraNavyCardElevated,
    onSurfaceVariant = RudraTextSecondary,

    outline = RudraNavyBorder,
    outlineVariant = Color(0xFF1B283F),

    error = RudraCoral,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
