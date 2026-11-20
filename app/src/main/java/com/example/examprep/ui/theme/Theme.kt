package com.example.examprep.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = PrimaryNavy,
    onPrimary = Color.White,
    primaryContainer = PrimaryNavyLight,
    onPrimaryContainer = Color.White,
    secondary = AccentOrange,
    onSecondary = Color.White,
    secondaryContainer = AccentOrangeLight,
    onSecondaryContainer = PrimaryNavyDark,
    tertiary = SuccessGreen,
    onTertiary = Color.White,
    tertiaryContainer = SuccessGreenLight,
    onTertiaryContainer = TextDark,
    background = PaperBackground,
    onBackground = TextDark,
    surface = PaperCard,
    onSurface = TextDark,
    surfaceVariant = PaperCardAlt,
    onSurfaceVariant = TextMuted,
    outline = PaperBorder,
    error = ErrorRed,
    onError = Color.White,
    errorContainer = ErrorRedLight,
    onErrorContainer = ErrorRed
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF6E94DF),
    onPrimary = Color(0xFF0F1E38),
    primaryContainer = PrimaryNavy,
    onPrimaryContainer = Color.White,
    secondary = Color(0xFFFFB27D),
    onSecondary = Color(0xFF4A2800),
    background = Color(0xFF141923),
    onBackground = Color(0xFFEDE8E0),
    surface = Color(0xFF1D2432),
    onSurface = Color(0xFFEDE8E0),
    surfaceVariant = Color(0xFF263042),
    onSurfaceVariant = Color(0xFFB5BDCA),
    outline = Color(0xFF374357)
)

@Composable
fun ExamPrepTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = colorScheme.primary.toArgb()
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
