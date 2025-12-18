package com.myg.material2048.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable

@Composable
actual fun getPlatformColorScheme(darkTheme: Boolean): ColorScheme? {
    // Desktop doesn't support dynamic colors natively yet.
    // Return null to fallback to static Light/Dark schemes defined in commonMain
    // or implement a custom logic here if needed.
    return null
}
