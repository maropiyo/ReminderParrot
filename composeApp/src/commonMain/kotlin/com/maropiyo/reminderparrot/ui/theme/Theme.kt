package com.maropiyo.reminderparrot.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

// ライトテーマ
private val LightColorScheme =
    lightColorScheme(
        primary = Primary,
        onPrimary = White,
        primaryContainer = Primary,
        onPrimaryContainer = White,
        secondary = Secondary,
        onSecondary = White,
        secondaryContainer = Secondary,
        onSecondaryContainer = White,
        tertiary = Tertiary,
        onTertiary = White,
        background = Background,
        onBackground = Black,
        surface = Background,
        onSurface = Black,
        surfaceVariant = White,
        onSurfaceVariant = Black,
        error = Error,
        onError = ErrorDark,
        errorContainer = ErrorLight,
        onErrorContainer = ErrorDark
    )

/**
 * ReminderParrotのテーマ
 */
@Composable
fun ReminderParrotTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}
