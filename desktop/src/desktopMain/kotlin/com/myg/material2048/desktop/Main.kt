package com.myg.material2048.desktop

import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.myg.material2048.shared.DesktopGameStorage
import com.myg.material2048.ui.GameScreen
import com.myg.material2048.ui.theme.Material2048Theme
import com.myg.material2048.viewmodel.GameViewModel
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.onKeyEvent
import com.myg.material2048.model.Direction

fun main() = application {
    val storage = DesktopGameStorage()
    val viewModel = GameViewModel(storage)

    Window(
        onCloseRequest = ::exitApplication,
        title = "Material 2048",
        state = rememberWindowState(width = 800.dp, height = 900.dp),
        onKeyEvent = { event ->
            if (event.type == KeyEventType.KeyDown) {
                when (event.key) {
                    Key.DirectionUp -> { viewModel.onMove(Direction.UP); true }
                    Key.DirectionDown -> { viewModel.onMove(Direction.DOWN); true }
                    Key.DirectionLeft -> { viewModel.onMove(Direction.LEFT); true }
                    Key.DirectionRight -> { viewModel.onMove(Direction.RIGHT); true }
                    Key.Z -> { 
                        // Ctrl+Z check could be added here
                        viewModel.undo(); true 
                    }
                    Key.R -> { viewModel.restartGame(); true }
                    else -> false
                }
            } else {
                false
            }
        }
    ) {
        Material2048Theme {
            GameScreen(gameViewModel = viewModel)
        }
    }
}
