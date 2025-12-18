package com.myg.material2048.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.File

@Serializable
private data class ColorsConfig(
    val accent1: List<String>,
    val accent2: List<String>,
    val accent3: List<String>,
    val neutral1: List<String>,
    val neutral2: List<String>
)

@Composable
actual fun getPlatformTileColors(value: Int): Pair<Color, Color> {
    return remember(value) {
        val config = loadColorsConfig()
        if (config != null) {
            getDynamicTileColors(value, config)
        } else {
            getStaticTileColors(value)
        }
    }
}

private fun loadColorsConfig(): ColorsConfig? {
    return try {
        val file = File("C:\\Program Files\\MaterialYouWindows\\colors")
        if (file.exists()) {
            val jsonContent = file.readText()
            Json.decodeFromString<ColorsConfig>(jsonContent)
        } else {
            null
        }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

private fun getDynamicTileColors(value: Int, config: ColorsConfig): Pair<Color, Color> {
    // Map 2048 values to tones in accent1
    // The input lists have 10 colors.
    // Index 0 is lightest (tone 100/90), Index 9 is darkest (tone 10).
    
    // We want light tiles for low numbers, dark tiles for high numbers? 
    // Or just distinct colors.
    // The original 2048 goes from light beige to orange/red/yellow.
    
    // Let's use accent1 for the tiles, shifting from light to dark.
    // The list has 10 items.
    
    val toneIndex = when (value) {
        2 -> 0 // Lightest
        4 -> 1
        8 -> 2
        16 -> 3
        32 -> 4
        64 -> 5
        128 -> 6
        256 -> 7
        512 -> 8
        1024 -> 9 // Darkest
        else -> 9
    }

    val backgroundColor = parseColor(config.accent1.getOrElse(toneIndex) { "#000000" })
    
    // Text color logic: Dark text for light backgrounds (indices 0-4), Light text for dark backgrounds (indices 5-9)
    val textColor = if (toneIndex <= 4) {
        // Dark text from neutral1 (index 9 is dark)
        parseColor(config.neutral1.getOrElse(9) { "#000000" })
    } else {
        // Light text from neutral1 (index 0 is light)
        parseColor(config.neutral1.getOrElse(0) { "#ffffff" })
    }

    return backgroundColor to textColor
}

private fun getStaticTileColors(value: Int): Pair<Color, Color> {
    return when (value) {
        2 -> Color(0xFFEEE4DA) to Color(0xFF776E65)
        4 -> Color(0xFFEDE0C8) to Color(0xFF776E65)
        8 -> Color(0xFFF2B179) to Color.White
        16 -> Color(0xFFF59563) to Color.White
        32 -> Color(0xFFF67C5F) to Color.White
        64 -> Color(0xFFF65E3B) to Color.White
        128 -> Color(0xFFEDCF72) to Color.White
        256 -> Color(0xFFEDCC61) to Color.White
        512 -> Color(0xFFEDC850) to Color.White
        1024 -> Color(0xFFEDC53F) to Color.White
        2048 -> Color(0xFFEDC22E) to Color.White
        else -> Color(0xFF3C3A32) to Color.White
    }
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
        Color.Magenta 
    }
}
