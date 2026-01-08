package com.myg.material2048.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable

@Composable
actual fun getPlatformColorScheme(darkTheme: Boolean): ColorScheme? {
    // Use default color scheme on web - no platform-specific dynamic colors
    return null
}

