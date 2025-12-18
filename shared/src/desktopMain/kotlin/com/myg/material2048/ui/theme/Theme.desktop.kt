package com.myg.material2048.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import java.io.File

@Composable
actual fun getPlatformColorScheme(darkTheme: Boolean): ColorScheme? {
    return remember(darkTheme) {
        loadCustomColorScheme()
    }
}

private fun loadCustomColorScheme(): ColorScheme? {
    try {
        val file = File("C:\\Program Files\\MaterialYouWindows\\colors")
        if (file.exists()) {
            val lines = file.readLines()
            val colors = mutableMapOf<String, Color>()

            for (line in lines) {
                if (line.startsWith("---")) break
                val parts = line.split("=")
                if (parts.size == 2) {
                    colors[parts[0].trim()] = parseColor(parts[1].trim())
                }
            }
            
            if (colors.isEmpty()) return null

            // Construct ColorScheme using values from file
            // Using default Color.Magenta for missing values to make errors obvious, 
            // though the file should contain all.
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
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return null
}

private fun parseColor(colorString: String): Color {
    return try {
        val hex = colorString.removePrefix("#")
        val longValue = java.lang.Long.parseLong(hex, 16)
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
