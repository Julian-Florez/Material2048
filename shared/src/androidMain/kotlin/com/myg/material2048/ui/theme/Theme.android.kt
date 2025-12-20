package com.myg.material2048.ui.theme

import android.os.Build
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

@Composable
actual fun getPlatformColorScheme(darkTheme: Boolean): ColorScheme? {
    // Return the custom static color scheme directly to improve startup performance
    return customColorScheme
}

// Static definition to avoid runtime parsing overhead
private val customColorScheme = ColorScheme(
    primary = Color(0xFFD7CA00),
    onPrimary = Color(0xFF353100),
    primaryContainer = Color(0xFF4D4800),
    onPrimaryContainer = Color(0xFFF6E600),
    inversePrimary = Color(0xFF676000),
    secondary = Color(0xFFC5CB96),
    onSecondary = Color(0xFF2E330D),
    secondaryContainer = Color(0xFF454A21),
    onSecondaryContainer = Color(0xFFE1E7B0),
    tertiary = Color(0xFFB5CF90),
    onTertiary = Color(0xFF223607),
    tertiaryContainer = Color(0xFF384D1C),
    onTertiaryContainer = Color(0xFFD1ECAA),
    background = Color(0xFF151407),
    onBackground = Color(0xFFE8E3CC),
    surface = Color(0xFF151407),
    onSurface = Color(0xFFE8E3CC),
    surfaceVariant = Color(0xFF4A4733),
    onSurfaceVariant = Color(0xFFCCC7AC),
    surfaceTint = Color(0xFFD7CA00),
    inverseSurface = Color(0xFFFFFAE3),
    inverseOnSurface = Color(0xFF1D1C0E),
    error = Color(0xFFF2B8B5),
    onError = Color(0xFF601410),
    errorContainer = Color(0xFF8C1D18),
    onErrorContainer = Color(0xFFF9DEDC),
    outline = Color(0xFF959179),
    outlineVariant = Color(0xFF4A4733),
    scrim = Color(0xFF000000),
    surfaceBright = Color(0xFF3C3A2A),
    surfaceDim = Color(0xFF151407),
    surfaceContainer = Color(0xFF222012),
    surfaceContainerHigh = Color(0xFF2C2A1B),
    surfaceContainerHighest = Color(0xFF373525),
    surfaceContainerLow = Color(0xFF1D1C0E),
    surfaceContainerLowest = Color(0xFF100F03)
)
