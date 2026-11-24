package com.example.examprep.ui.theme

import android.graphics.Color as AndroidColor
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import com.example.examprep.model.AppThemeMode

@Immutable
data class ExamFeedbackColors(
    val correct: Color,
    val correctContainer: Color,
    val wrong: Color,
    val wrongContainer: Color,
    val selectedBorder: Color,
    val selectedContainer: Color
)

val LocalExamFeedbackColors = staticCompositionLocalOf {
    ExamFeedbackColors(
        correct = SuccessGreen,
        correctContainer = SuccessGreenLight,
        wrong = ErrorRed,
        wrongContainer = ErrorRedLight,
        selectedBorder = PrimaryNavy,
        selectedContainer = Color(0xFFEAF0FA)
    )
}

private val PaperColorScheme = lightColorScheme(
    primary = PrimaryNavy,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE5ECF8),
    onPrimaryContainer = PrimaryNavyDark,
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
    primary = DarkPrimary,
    onPrimary = Color(0xFF0A1936),
    primaryContainer = DarkPrimaryContainer,
    onPrimaryContainer = Color(0xFFDCE7FF),
    secondary = DarkAccentOrange,
    onSecondary = Color(0xFF3A1C00),
    secondaryContainer = DarkAccentContainer,
    onSecondaryContainer = Color(0xFFFFD8B8),
    tertiary = SuccessGreenDarkText,
    onTertiary = Color(0xFF06291A),
    tertiaryContainer = SuccessGreenDarkBg,
    onTertiaryContainer = SuccessGreenDarkText,
    background = DarkBackground,
    onBackground = DarkTextPrimary,
    surface = DarkCard,
    onSurface = DarkTextPrimary,
    surfaceVariant = DarkCardAlt,
    onSurfaceVariant = DarkTextMuted,
    outline = DarkBorder,
    error = ErrorRedDarkText,
    onError = Color(0xFF3F0A0A),
    errorContainer = ErrorRedDarkBg,
    onErrorContainer = ErrorRedDarkText
)

@Composable
fun ExamPrepTheme(
    themeMode: AppThemeMode = AppThemeMode.PAPER,
    content: @Composable () -> Unit
) {
    val isDark = themeMode == AppThemeMode.DARK
    val colorScheme = if (isDark) DarkColorScheme else PaperColorScheme
    val feedbackColors = if (isDark) {
        ExamFeedbackColors(
            correct = SuccessGreenDarkText,
            correctContainer = SuccessGreenDarkBg,
            wrong = ErrorRedDarkText,
            wrongContainer = ErrorRedDarkBg,
            selectedBorder = DarkPrimary,
            selectedContainer = DarkPrimaryContainer
        )
    } else {
        ExamFeedbackColors(
            correct = SuccessGreen,
            correctContainer = SuccessGreenLight,
            wrong = ErrorRed,
            wrongContainer = ErrorRedLight,
            selectedBorder = PrimaryNavy,
            selectedContainer = Color(0xFFEAF0FA)
        )
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val activity = view.context as? ComponentActivity
            if (activity != null) {
                val barStyle = if (isDark) {
                    SystemBarStyle.dark(AndroidColor.TRANSPARENT)
                } else {
                    SystemBarStyle.light(
                        scrim = AndroidColor.TRANSPARENT,
                        darkScrim = AndroidColor.TRANSPARENT
                    )
                }
                activity.enableEdgeToEdge(
                    statusBarStyle = barStyle,
                    navigationBarStyle = barStyle
                )
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    activity.window.isNavigationBarContrastEnforced = false
                }
            }
        }
    }

    CompositionLocalProvider(LocalExamFeedbackColors provides feedbackColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
