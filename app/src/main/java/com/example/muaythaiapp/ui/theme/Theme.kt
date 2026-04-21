package com.example.muaythaiapp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = DeepCrimson,
    onPrimary = BoneWhite,
    secondary = BurnoutCrimson,
    onSecondary = BoneWhite,
    tertiary = CornerBlack,
    onTertiary = BoneWhite,
    background = MatteBlack,
    onBackground = BoneWhite,
    surface = CageBlack,
    onSurface = BoneWhite,
    surfaceVariant = CornerBlack,
    onSurfaceVariant = SteelGray,
    outline = DeepCrimson.copy(alpha = 0.55f),
)

private val LightColorScheme = lightColorScheme(
    primary = DeepCrimson,
    onPrimary = BoneWhite,
    secondary = BurnoutCrimson,
    onSecondary = BoneWhite,
    tertiary = CornerBlack,
    onTertiary = BoneWhite,
    background = CanvasLight,
    onBackground = MatteBlack,
    surface = SurfaceLight,
    onSurface = MatteBlack,
    surfaceVariant = Color(0xFFE6DED0),
    onSurfaceVariant = Color(0xFF5D544D),
    outline = DeepCrimson.copy(alpha = 0.32f),
)

@Suppress("UNUSED_PARAMETER")
@Composable
fun MuayThaiAPPTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        typography = Typography,
        content = content,
    )
}
