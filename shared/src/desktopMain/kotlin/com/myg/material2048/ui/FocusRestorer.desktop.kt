package com.myg.material2048.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.focus.FocusRequester

@Composable
actual fun ObservePlatformFocusLifecycle(focusRequester: FocusRequester) {
    // Desktop platforms keep focus unless the window changes; no extra handling needed.
}

