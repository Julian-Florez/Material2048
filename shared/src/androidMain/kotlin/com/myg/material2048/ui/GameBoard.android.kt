package com.myg.material2048.ui

import android.os.Build
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

@Composable
actual fun getPlatformTileColors(value: Int): Pair<Color, Color> {
    val context = LocalContext.current
    val colorScheme = MaterialTheme.colorScheme
    
    // Only use dynamic colors on Android 12+ (S) and if supported
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        remember(value, context, colorScheme) {
            val tone = when (value) {
                2 -> 50
                4 -> 100
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

            val colorId = context.resources.getIdentifier("system_accent1_$tone", "color", "android")
            
            val backgroundColor = if (colorId != 0) {
                Color(context.resources.getColor(colorId, context.theme))
            } else {
                colorScheme.primaryContainer
            }

            // Obtener colores "accent" profundos del sistema para el texto
            val textLightId = context.resources.getIdentifier("system_accent1_100", "color", "android")
            val textDarkId = context.resources.getIdentifier("system_accent1_800", "color", "android")

            val textLight = if (textLightId != 0) Color(context.resources.getColor(textLightId, context.theme)) else Color.White
            val textDark = if (textDarkId != 0) Color(context.resources.getColor(textDarkId, context.theme)) else Color.Black

            // Ajuste de contraste
            val textColor = if (tone <= 400) textDark else textLight

            backgroundColor to textColor
        }
    } else {
        // Fallback for older Android versions
        getStaticTileColors(value)
    }
}

fun getStaticTileColors(value: Int): Pair<Color, Color> {
    // Basic fallback colors (same as desktop/default)
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
