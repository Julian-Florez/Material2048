package com.myg.material2048.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.File

@Serializable
data class ColorsConfig(
    val accent1: List<String>,
    val accent2: List<String>,
    val accent3: List<String>,
    val neutral1: List<String>,
    val neutral2: List<String>
)

@Composable
actual fun getPlatformColorScheme(darkTheme: Boolean): ColorScheme? {
    return remember(darkTheme) {
        loadCustomColorScheme(darkTheme)
    }
}

private fun loadCustomColorScheme(darkTheme: Boolean): ColorScheme? {
    try {
        val file = File("C:\\Program Files\\MaterialYouWindows\\colors")
        if (file.exists()) {
            val jsonContent = file.readText()
            val colorsConfig = Json.decodeFromString<ColorsConfig>(jsonContent)

            // Map Material You tones to Material 3 ColorScheme
            // This is an approximation based on Material Design 3 mapping logic
            return if (darkTheme) {
                darkColorScheme(
                    primary = parseColor(colorsConfig.accent1[2]), // tone 80
                    onPrimary = parseColor(colorsConfig.accent1[8]), // tone 20
                    primaryContainer = parseColor(colorsConfig.accent1[7]), // tone 30
                    onPrimaryContainer = parseColor(colorsConfig.accent1[1]), // tone 90
                    
                    secondary = parseColor(colorsConfig.accent2[2]), // tone 80
                    onSecondary = parseColor(colorsConfig.accent2[8]), // tone 20
                    secondaryContainer = parseColor(colorsConfig.accent2[7]), // tone 30
                    onSecondaryContainer = parseColor(colorsConfig.accent2[1]), // tone 90
                    
                    tertiary = parseColor(colorsConfig.accent3[2]), // tone 80
                    onTertiary = parseColor(colorsConfig.accent3[8]), // tone 20
                    tertiaryContainer = parseColor(colorsConfig.accent3[7]), // tone 30
                    onTertiaryContainer = parseColor(colorsConfig.accent3[1]), // tone 90
                    
                    background = parseColor(colorsConfig.neutral1[9]), // tone 10 (approx)
                    onBackground = parseColor(colorsConfig.neutral1[1]), // tone 90
                    surface = parseColor(colorsConfig.neutral1[9]), // tone 10
                    onSurface = parseColor(colorsConfig.neutral1[1]), // tone 90
                    surfaceVariant = parseColor(colorsConfig.neutral2[7]), // tone 30
                    onSurfaceVariant = parseColor(colorsConfig.neutral2[2]), // tone 80
                    outline = parseColor(colorsConfig.neutral2[4]) // tone 60 (approx)
                )
            } else {
                lightColorScheme(
                    primary = parseColor(colorsConfig.accent1[6]), // tone 40
                    onPrimary = parseColor(colorsConfig.neutral1[0]), // tone 100 (white)
                    primaryContainer = parseColor(colorsConfig.accent1[1]), // tone 90
                    onPrimaryContainer = parseColor(colorsConfig.accent1[9]), // tone 10
                    
                    secondary = parseColor(colorsConfig.accent2[6]), // tone 40
                    onSecondary = parseColor(colorsConfig.neutral1[0]), // tone 100
                    secondaryContainer = parseColor(colorsConfig.accent2[1]), // tone 90
                    onSecondaryContainer = parseColor(colorsConfig.accent2[9]), // tone 10
                    
                    tertiary = parseColor(colorsConfig.accent3[6]), // tone 40
                    onTertiary = parseColor(colorsConfig.neutral1[0]), // tone 100
                    tertiaryContainer = parseColor(colorsConfig.accent3[1]), // tone 90
                    onTertiaryContainer = parseColor(colorsConfig.accent3[9]), // tone 10
                    
                    background = parseColor(colorsConfig.neutral1[1]), // tone 99/98 (approx tone 90 from list) - Adjust index if needed
                    onBackground = parseColor(colorsConfig.neutral1[9]), // tone 10
                    surface = parseColor(colorsConfig.neutral1[1]), // tone 99/98
                    onSurface = parseColor(colorsConfig.neutral1[9]), // tone 10
                    surfaceVariant = parseColor(colorsConfig.neutral2[1]), // tone 90
                    onSurfaceVariant = parseColor(colorsConfig.neutral2[7]), // tone 30
                    outline = parseColor(colorsConfig.neutral2[5]) // tone 50
                )
            }
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
        if (hex.length == 6) {
            Color(longValue or 0xFF000000)
        } else {
            Color(longValue)
        }
    } catch (ex: Exception) {
        Color.Magenta // Error color
    }
}
