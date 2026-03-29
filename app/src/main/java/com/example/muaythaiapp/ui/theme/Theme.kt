package com.example.muaythaiapp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = FightRed,
    onPrimary = PureWhite,
    secondary = FightGold,
    onSecondary = DeepInk,
    tertiary = SuccessGreen,
    onTertiary = DeepInk,
    background = CanvasDark,
    onBackground = PureWhite,
    surface = SurfaceDark,
    onSurface = PureWhite,
    surfaceVariant = DeepInk,
    onSurfaceVariant = GuardGray
)

private val LightColorScheme = lightColorScheme(
    primary = FightRed,
    onPrimary = PureWhite,
    secondary = FightGold,
    onSecondary = DeepInk,
    tertiary = SuccessGreen,
    onTertiary = DeepInk,
    background = CanvasLight,
    onBackground = DeepInk,
    surface = SurfaceLight,
    onSurface = DeepInk,
    surfaceVariant = Color(0xFFE6DED0),
    onSurfaceVariant = Color(0xFF5D544D)
)

@Suppress("UNUSED_PARAMETER")
@Composable
fun MuayThaiAPPTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        typography = Typography,
        content = content
    )
}
