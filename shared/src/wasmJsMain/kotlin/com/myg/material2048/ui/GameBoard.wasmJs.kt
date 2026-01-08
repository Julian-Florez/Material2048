package com.myg.material2048.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color

private const val COLORS_CONFIG_STRING = """primary=#FFFFA44E onPrimary=#FF552C00 primaryContainer=#FFFE8F00 onPrimaryContainer=#FF462300 inversePrimary=#FF8A4C00 secondary=#FFFAA323 onSecondary=#FF4D2E00 secondaryContainer=#FF865300 onSecondaryContainer=#FFFFF6F0 tertiary=#FFFFE79B onTertiary=#FF665300 tertiaryContainer=#FFFED73E onTertiaryContainer=#FF5C4B00 background=#FF180B00 onBackground=#FFFFE0C0 surface=#FF180B00 onSurface=#FFFFE0C0 surfaceVariant=#FF382000 onSurfaceVariant=#FFB78B57 surfaceTint=#FFFFA44Ee inverseSurface=#FFFFF5ED inverseOnSurface=#FF452800 error=#FFF2B8B5 onError=#FF601410 errorContainer=#FF8C1D18 onErrorContainer=#FFF9DEDC outline=#FF966E3D outlineVariant=#FF624114 scrim=#FF000000 surfaceBright=#FF412600 surfaceDim=#FF180B00 surfaceContainer=#FF281500 surfaceContainerHigh=#FF301B00 surfaceContainerHighest=#FF382000 surfaceContainerLow=#FF1F1000 surfaceContainerLowest=#FF000000  --- System Colors Table --- Tone,Accent1,Accent2,Accent3,Neutral1,Neutral2 0,#FFFFFFFF,#FFFFFFFF,#FFFFFFFF,#FFFFFFFF,#FFFFFFFF 10,#FFFFFBFF,#FFFFFBFF,#FFFFFBFF,#FFFFFBFF,#FFFFFBFF 50,#FFFFEEE2,#FFFFEEDE,#FFFFF0C7,#FFFFEEDE,#FFFFEEDE 100,#FFFFDCC1,#FFFFDDB8,#FFFFE17A,#FFFFDDB8,#FFFFDDB8 200,#FFFFB779,#FFFFB960,#FFE9C329,#FFEFBE85,#FFF8BB71 300,#FFFE8F00,#FFED9915,#FFCBA800,#FFD1A36D,#FFDAA059 400,#FFD87900,#FFCA8000,#FFAC8E00,#FFB48955,#FFBC8641 500,#FFB26300,#FFA76900,#FF8E7500,#FF986F3E,#FF9F6D2A 600,#FF8F4E00,#FF865300,#FF715C00,#FF7C5728,#FF825513 700,#FF6C3A00,#FF653E00,#FF554500,#FF614013,#FF653E00 800,#FF4C2700,#FF472A00,#FF3B2F00,#FF472A00,#FF472A00 900,#FF2E1500,#FF2B1700,#FF231B00,#FF2B1700,#FF2B1700 1000,#FF000000,#FF000000,#FF000000,#FF000000,#FF000000 """

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
        val lines = COLORS_CONFIG_STRING.split(" ")
        val accent1Map = mutableMapOf<Int, String>()

        var inTable = false
        for (line in lines) {
            if (line.startsWith("---")) {
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
    } catch (e: Exception) {
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
        val longValue = hex.toLong(16)
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


