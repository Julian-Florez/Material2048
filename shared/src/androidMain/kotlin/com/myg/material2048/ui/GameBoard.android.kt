package com.myg.material2048.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Static configuration to ensure zero-overhead startup and consistent performance
private val SYSTEM_ACCENT1_PALETTE = mapOf(
    50 to Color(0xFFFFF482),
    100 to Color(0xFFF6E600),
    200 to Color(0xFFD7CA00),
    300 to Color(0xFFBAAE00),
    400 to Color(0xFF9D9300),
    500 to Color(0xFF827900),
    600 to Color(0xFF676000),
    700 to Color(0xFF4D4800),
    800 to Color(0xFF353100),
    900 to Color(0xFF1F1C00),
    1000 to Color(0xFF000000)
)

private val TEXT_LIGHT = SYSTEM_ACCENT1_PALETTE[100]!!
private val TEXT_DARK = SYSTEM_ACCENT1_PALETTE[800]!!

// Pre-computed cache of colors for each tile value
private val TILE_COLOR_CACHE: Map<Int, Pair<Color, Color>> = run {
    val map = mutableMapOf<Int, Pair<Color, Color>>()
    
    // Mapping of Game Value -> Tone
    val valueToTone = mapOf(
        2 to 50,
        4 to 100,
        8 to 200,
        16 to 300,
        32 to 400,
        64 to 500,
        128 to 600,
        256 to 700,
        512 to 800,
        1024 to 900
    )

    // Populate cache for standard values
    valueToTone.forEach { (value, tone) ->
        val bg = SYSTEM_ACCENT1_PALETTE[tone] ?: Color.Black
        // Contrast logic: Tone <= 400 uses Dark Text, otherwise Light Text
        val fg = if (tone <= 400) TEXT_DARK else TEXT_LIGHT
        map[value] = bg to fg
    }
    
    // Default fallback for higher values (Tone 1000)
    val defaultBg = SYSTEM_ACCENT1_PALETTE[1000]!!
    val defaultFg = TEXT_LIGHT
    map[-1] = defaultBg to defaultFg
    
    map
}

@Composable
actual fun getPlatformTileColors(value: Int): Pair<Color, Color> {
    // Immediate lookup, no parsing, no context access, no object allocation per call (cached pairs)
    return TILE_COLOR_CACHE[value] ?: TILE_COLOR_CACHE[-1]!!
}

fun getStaticTileColors(value: Int): Pair<Color, Color> {
    // Legacy/Fallback colors
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
