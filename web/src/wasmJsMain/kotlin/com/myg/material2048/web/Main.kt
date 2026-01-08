package com.myg.material2048.web

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.CanvasBasedWindow
import com.myg.material2048.shared.WebGameStorage
import com.myg.material2048.ui.GameScreen
import com.myg.material2048.ui.theme.Material2048Theme
import com.myg.material2048.viewmodel.GameViewModel

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    val storage = WebGameStorage()
    val viewModel = GameViewModel(storage)

    CanvasBasedWindow(canvasElementId = "ComposeTarget", title = "Material 2048") {
        Material2048Theme {
            GameScreen(gameViewModel = viewModel)
        }
    }
}

