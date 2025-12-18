package com.myg.material2048.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import java.io.File

data class ColorsConfig(
    val accent1: Map<Int, String>
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
            val lines = file.readLines()
            val accent1Map = mutableMapOf<Int, String>()
            
            var inTable = false
            for (line in lines) {
                if (line.startsWith("--- System Colors Table ---")) {
                    inTable = true
                    continue
                }
                if (inTable) {
                    if (line.startsWith("Tone")) continue
                    
                    // Format: Tone,Accent1,Accent2,Accent3,Neutral1,Neutral2
                    // Example: 100,#FFFFDCC1,#FFFFDDB8,...
                    val parts = line.split(",")
                    if (parts.size >= 2) {
                        val tone = parts[0].trim().toIntOrNull()
                        val accent1 = parts[1].trim()
                        
                        if (tone != null) {
                            accent1Map[tone] = accent1
                        }
                    }
                }
            }
            
            if (accent1Map.isNotEmpty()) {
                ColorsConfig(accent1Map)
            } else {
                null
            }
        } else {
            null
        }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

private fun getDynamicTileColors(value: Int, config: ColorsConfig): Pair<Color, Color> {
    val tone = when (value) {
        2 -> 50     // Android 2 -> 50
        4 -> 100    // Android 4 -> 100
        8 -> 200
        16 -> 300
        32 -> 400
        64 -> 500
        128 -> 600
        256 -> 700
        512 -> 800
        1024 -> 900
        else -> 1000
    }

    val backgroundColorString = config.accent1[tone] ?: "#000000"
    val backgroundColor = parseColor(backgroundColorString)
    
    // Text colors:
    // Light text -> Tone 100
    // Dark text -> Tone 800
    // Note: If 50 is lighter than 100, usually 100 is light enough.
    // However, if we follow Android logic strictly:
    // textLightId = system_accent1_100
    // textDarkId = system_accent1_800
    
    val textLightString = config.accent1[100] ?: "#ffffff"
    val textDarkString = config.accent1[800] ?: "#000000"
    
    val textLight = parseColor(textLightString)
    val textDark = parseColor(textDarkString)

    // Contrast adjustment
    // if tone <= 400 use textDark, else use textLight
    val textColor = if (tone <= 400) textDark else textLight

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
