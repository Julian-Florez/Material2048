package com.myg.material2048.ui

import androidx.compose.ui.graphics.Color

/**
 * Shared representation of the Material You color dump used by desktop/web platforms.
 */
data class ColorsConfig(
    val namedColors: Map<String, String>,
    val accent1: Map<Int, String>
)

fun parseColorsConfigLines(lines: List<String>): ColorsConfig? {
    val namedColors = mutableMapOf<String, String>()
    val accent1 = mutableMapOf<Int, String>()
    var isParsingTable = false

    for (line in lines) {
        val trimmedLine = line.trim()
        if (trimmedLine.isEmpty()) continue

        if (!isParsingTable) {
            if (trimmedLine.startsWith("--- System Colors Table ---")) {
                isParsingTable = true
                continue
            }

            val parts = trimmedLine.split("=")
            if (parts.size == 2) {
                namedColors[parts[0].trim()] = parts[1].trim()
            }
        } else {
            if (trimmedLine.startsWith("Tone")) continue
            val parts = trimmedLine.split(",")
            if (parts.size >= 2) {
                val tone = parts[0].trim().toIntOrNull()
                val accent = parts[1].trim()
                if (tone != null) {
                    accent1[tone] = accent
                }
            }
        }
    }

    if (namedColors.isEmpty() && accent1.isEmpty()) return null
    return ColorsConfig(namedColors, accent1)
}

fun parseColorsConfigString(raw: String): ColorsConfig? {
    val normalized = raw.replace("\r\n", "\n")
    return parseColorsConfigLines(normalized.lines())
}

fun parseColor(colorString: String): Color {
    return try {
        val hex = colorString.removePrefix("#")
        val longValue = hex.toLong(16)
        when (hex.length) {
            8 -> Color(longValue)
            6 -> Color(longValue or 0xFF000000)
            else -> Color.Magenta
        }
    } catch (ex: Exception) {
        Color.Magenta
    }
}

