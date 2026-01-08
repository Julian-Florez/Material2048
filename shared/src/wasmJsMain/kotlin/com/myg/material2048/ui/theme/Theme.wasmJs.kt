package com.myg.material2048.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color

private const val COLORS_CONFIG_STRING = """primary=#FFFFA44E onPrimary=#FF552C00 primaryContainer=#FFFE8F00 onPrimaryContainer=#FF462300 inversePrimary=#FF8A4C00 secondary=#FFFAA323 onSecondary=#FF4D2E00 secondaryContainer=#FF865300 onSecondaryContainer=#FFFFF6F0 tertiary=#FFFFE79B onTertiary=#FF665300 tertiaryContainer=#FFFED73E onTertiaryContainer=#FF5C4B00 background=#FF180B00 onBackground=#FFFFE0C0 surface=#FF180B00 onSurface=#FFFFE0C0 surfaceVariant=#FF382000 onSurfaceVariant=#FFB78B57 surfaceTint=#FFFFA44Ee inverseSurface=#FFFFF5ED inverseOnSurface=#FF452800 error=#FFF2B8B5 onError=#FF601410 errorContainer=#FF8C1D18 onErrorContainer=#FFF9DEDC outline=#FF966E3D outlineVariant=#FF624114 scrim=#FF000000 surfaceBright=#FF412600 surfaceDim=#FF180B00 surfaceContainer=#FF281500 surfaceContainerHigh=#FF301B00 surfaceContainerHighest=#FF382000 surfaceContainerLow=#FF1F1000 surfaceContainerLowest=#FF000000  --- System Colors Table --- Tone,Accent1,Accent2,Accent3,Neutral1,Neutral2 0,#FFFFFFFF,#FFFFFFFF,#FFFFFFFF,#FFFFFFFF,#FFFFFFFF 10,#FFFFFBFF,#FFFFFBFF,#FFFFFBFF,#FFFFFBFF,#FFFFFBFF 50,#FFFFEEE2,#FFFFEEDE,#FFFFF0C7,#FFFFEEDE,#FFFFEEDE 100,#FFFFDCC1,#FFFFDDB8,#FFFFE17A,#FFFFDDB8,#FFFFDDB8 200,#FFFFB779,#FFFFB960,#FFE9C329,#FFEFBE85,#FFF8BB71 300,#FFFE8F00,#FFED9915,#FFCBA800,#FFD1A36D,#FFDAA059 400,#FFD87900,#FFCA8000,#FFAC8E00,#FFB48955,#FFBC8641 500,#FFB26300,#FFA76900,#FF8E7500,#FF986F3E,#FF9F6D2A 600,#FF8F4E00,#FF865300,#FF715C00,#FF7C5728,#FF825513 700,#FF6C3A00,#FF653E00,#FF554500,#FF614013,#FF653E00 800,#FF4C2700,#FF472A00,#FF3B2F00,#FF472A00,#FF472A00 900,#FF2E1500,#FF2B1700,#FF231B00,#FF2B1700,#FF2B1700 1000,#FF000000,#FF000000,#FF000000,#FF000000,#FF000000 """

@Composable
actual fun getPlatformColorScheme(darkTheme: Boolean): ColorScheme? {
    return remember(darkTheme) {
        loadCustomColorScheme()
    }
}

private fun loadCustomColorScheme(): ColorScheme? {
    try {
        val lines = COLORS_CONFIG_STRING.split(" ")
        val colors = mutableMapOf<String, Color>()

        for (line in lines) {
            if (line.startsWith("---")) break
            val parts = line.split("=")
            if (parts.size == 2) {
                colors[parts[0].trim()] = parseColor(parts[1].trim())
            }
        }

        if (colors.isEmpty()) return null

        // Construct ColorScheme using values from string
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
    } catch (e: Exception) {
        return null
    }
}

private fun parseColor(colorString: String): Color {
    return try {
        val hex = colorString.removePrefix("#")
        val longValue = hex.toLong(16)
        // Handle 8-digit hex (ARGB) or 6-digit hex (RGB)
        if (hex.length == 8) {
            Color(longValue)
        } else if (hex.length == 6) {
            Color(longValue or 0xFF000000)
        } else {
            Color.Magenta
        }
    } catch (ex: Exception) {
        Color.Magenta
    }
}


