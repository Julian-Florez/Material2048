package com.myg.material2048.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.focus.FocusRequester

@Composable
actual fun ObservePlatformFocusLifecycle(focusRequester: FocusRequester) {
    // Android already manages focus for us.
}

