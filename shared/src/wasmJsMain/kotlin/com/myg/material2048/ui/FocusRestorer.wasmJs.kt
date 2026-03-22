package com.myg.material2048.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.focus.FocusRequester
import kotlinx.browser.document
import kotlinx.browser.window
import org.w3c.dom.events.Event

@Composable
actual fun ObservePlatformFocusLifecycle(focusRequester: FocusRequester) {
    DisposableEffect(focusRequester) {
        val requestFocus = {
            focusRequester.requestFocus()
        }

        val focusListener: (Event) -> Unit = {
            requestFocus()
        }
        val pageShowListener: (Event) -> Unit = {
            requestFocus()
        }
        val popStateListener: (Event) -> Unit = {
            requestFocus()
        }
        val visibilityListener: (Event) -> Unit = {
            requestFocus()
        }

        window.addEventListener("focus", focusListener)
        window.addEventListener("pageshow", pageShowListener)
        window.addEventListener("popstate", popStateListener)
        document.addEventListener("visibilitychange", visibilityListener)

        onDispose {
            window.removeEventListener("focus", focusListener)
            window.removeEventListener("pageshow", pageShowListener)
            window.removeEventListener("popstate", popStateListener)
            document.removeEventListener("visibilitychange", visibilityListener)
        }
    }
}
