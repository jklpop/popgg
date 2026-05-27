package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = EmeraldGreen,
    secondary = SoftGold,
    tertiary = EmeraldLight,
    background = DarkBackground,
    surface = MidnightBlue,
    onPrimary = Color.White,
    onSecondary = DarkBackground,
    onBackground = CreamWhite,
    onSurface = CreamWhite,
    primaryContainer = CardOverlayDark,
    secondaryContainer = EmeraldGreen.copy(alpha = 0.2f)
)

private val LightColorScheme = lightColorScheme(
    primary = EmeraldGreen,
    secondary = SoftGold,
    tertiary = MidnightBlue,
    background = CreamWhite,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = MidnightBlue,
    onSurface = MidnightBlue,
    primaryContainer = Color(0xFFE2EDE9),
    secondaryContainer = Color(0xFFFFF8E7)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    primaryAccent: Color = EmeraldGreen,
    content: @Composable () -> Unit,
) {
    val baseScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    // Dynamically adjust primary accent if user changes it in settings (Emerald, Blue, Gold)
    val colorScheme = baseScheme.copy(
        primary = primaryAccent,
        secondary = if (primaryAccent == SoftGold) EmeraldGreen else SoftGold,
        tertiary = if (primaryAccent == MidnightBlue) EmeraldGreen else MidnightBlue
    )

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
