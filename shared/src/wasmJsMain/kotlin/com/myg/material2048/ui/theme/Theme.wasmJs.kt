package com.myg.material2048.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import com.myg.material2048.ui.ColorsConfig
import com.myg.material2048.ui.EMBEDDED_COLORS_CONFIG
import com.myg.material2048.ui.parseColor
import com.myg.material2048.ui.parseColorsConfigString

@Composable
actual fun getPlatformColorScheme(darkTheme: Boolean): ColorScheme? {
    return remember(darkTheme) {
        loadCustomColorScheme()
    }
}

private fun loadCustomColorScheme(): ColorScheme? {
    val config = parseColorsConfigString(EMBEDDED_COLORS_CONFIG) ?: return null
    return toColorScheme(config)
}

private fun toColorScheme(config: ColorsConfig): ColorScheme {
    val colors = config.namedColors.mapValues { parseColor(it.value) }
    return ColorScheme(
        primary = colors["primary"] ?: Color.Magenta,
        onPrimary = colors["onPrimary"] ?: Color.Magenta,
        primaryContainer = colors["primaryContainer"] ?: Color.Magenta,
        onPrimaryContainer = colors["onPrimaryContainer"] ?: Color.Magenta,
        inversePrimary = colors["inversePrimary"] ?: Color.Magenta,
        secondary = colors["secondary"] ?: Color.Magenta,
        onSecondary = colors["onSecondary"] ?: Color.Magenta,
        secondaryContainer = colors["secondaryContainer"] ?: Color.Magenta,
        onSecondaryContainer = colors["onSecondaryContainer"] ?: Color.Magenta,
        tertiary = colors["tertiary"] ?: Color.Magenta,
        onTertiary = colors["onTertiary"] ?: Color.Magenta,
        tertiaryContainer = colors["tertiaryContainer"] ?: Color.Magenta,
        onTertiaryContainer = colors["onTertiaryContainer"] ?: Color.Magenta,
        background = colors["background"] ?: Color.Magenta,
        onBackground = colors["onBackground"] ?: Color.Magenta,
        surface = colors["surface"] ?: Color.Magenta,
        onSurface = colors["onSurface"] ?: Color.Magenta,
        surfaceVariant = colors["surfaceVariant"] ?: Color.Magenta,
        onSurfaceVariant = colors["onSurfaceVariant"] ?: Color.Magenta,
        surfaceTint = colors["surfaceTint"] ?: colors["primary"] ?: Color.Magenta,
        inverseSurface = colors["inverseSurface"] ?: Color.Magenta,
        inverseOnSurface = colors["inverseOnSurface"] ?: Color.Magenta,
        error = colors["error"] ?: Color.Magenta,
        onError = colors["onError"] ?: Color.Magenta,
        errorContainer = colors["errorContainer"] ?: Color.Magenta,
        onErrorContainer = colors["onErrorContainer"] ?: Color.Magenta,
        outline = colors["outline"] ?: Color.Magenta,
        outlineVariant = colors["outlineVariant"] ?: Color.Magenta,
        scrim = colors["scrim"] ?: Color.Magenta,
        surfaceBright = colors["surfaceBright"] ?: colors["surface"] ?: Color.Magenta,
        surfaceDim = colors["surfaceDim"] ?: colors["surface"] ?: Color.Magenta,
        surfaceContainer = colors["surfaceContainer"] ?: colors["surface"] ?: Color.Magenta,
        surfaceContainerHigh = colors["surfaceContainerHigh"] ?: colors["surface"] ?: Color.Magenta,
        surfaceContainerHighest = colors["surfaceContainerHighest"] ?: colors["surface"] ?: Color.Magenta,
        surfaceContainerLow = colors["surfaceContainerLow"] ?: colors["surface"] ?: Color.Magenta,
        surfaceContainerLowest = colors["surfaceContainerLowest"] ?: colors["surface"] ?: Color.Magenta,
    )
}
