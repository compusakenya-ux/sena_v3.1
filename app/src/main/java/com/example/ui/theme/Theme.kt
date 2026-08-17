package com.example.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val SenaDarkColorScheme = darkColorScheme(
    primary = SenaPeach,
    onPrimary = SenaBackgroundDark,
    primaryContainer = SenaOrangeGlow,
    onPrimaryContainer = SenaPeach,
    secondary = SenaElectricCyan,
    onSecondary = SenaBackgroundDark,
    secondaryContainer = SenaElectricGlow,
    onSecondaryContainer = SenaElectricCyan,
    background = SenaBackgroundDark,
    onBackground = SenaTextPrimaryDark,
    surface = SenaSurfaceDark,
    onSurface = SenaTextPrimaryDark,
    surfaceVariant = SenaSurfaceVariantDark,
    onSurfaceVariant = SenaTextSecondaryDark,
    outline = SenaBorderDark
)

private val SenaLightColorScheme = lightColorScheme(
    primary = SenaOrangeCTA,
    onPrimary = Color.White,
    primaryContainer = SenaPeach.copy(alpha = 0.2f),
    onPrimaryContainer = SenaOrangeCTA,
    secondary = Color(0xFF0284C7),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE0F2FE),
    onSecondaryContainer = Color(0xFF0369A1),
    background = SenaBackgroundLight,
    onBackground = SenaTextPrimaryLight,
    surface = SenaSurfaceLight,
    onSurface = SenaTextPrimaryLight,
    surfaceVariant = SenaSurfaceVariantLight,
    onSurfaceVariant = SenaTextSecondaryLight,
    outline = SenaBorderLight
)

@Composable
fun SenaTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) SenaDarkColorScheme else SenaLightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            val bg = (if (darkTheme) SenaBackgroundDark else SenaBackgroundLight).toArgb()
            window.statusBarColor = bg
            window.navigationBarColor = bg
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

