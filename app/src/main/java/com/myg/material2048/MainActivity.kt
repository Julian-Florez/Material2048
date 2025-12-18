package com.myg.material2048

import android.os.Bundle
import android.view.KeyEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.myg.material2048.model.Direction
import com.myg.material2048.ui.GameScreen
import com.myg.material2048.ui.theme.Material2048Theme
import com.myg.material2048.viewmodel.GameViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val viewModel: GameViewModel by viewModels()
    private var isMenuVisible = false
    private var canUndo = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    isMenuVisible = state.isGameOver || (state.hasWon && !state.keepPlaying)
                    canUndo = state.canUndo
                }
            }
        }

        setContent {
            Material2048Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    GameScreen(gameViewModel = viewModel)
                }
            }
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // Start tracking to detect long press
            event.startTracking()
            return true
        }

        return when (keyCode) {
            KeyEvent.KEYCODE_BUTTON_Y,
            KeyEvent.KEYCODE_SHIFT_LEFT,
            KeyEvent.KEYCODE_SHIFT_RIGHT -> {
                viewModel.undo()
                true
            }
            
            KeyEvent.KEYCODE_BUTTON_B,
            KeyEvent.KEYCODE_CTRL_LEFT,
            KeyEvent.KEYCODE_CTRL_RIGHT -> {
                viewModel.restartGame()
                true
            }

            KeyEvent.KEYCODE_DPAD_UP -> {
                if (!isMenuVisible) { viewModel.onMove(Direction.UP); true } else super.onKeyDown(keyCode, event)
            }
            KeyEvent.KEYCODE_DPAD_DOWN -> {
                if (!isMenuVisible) { viewModel.onMove(Direction.DOWN); true } else super.onKeyDown(keyCode, event)
            }
            KeyEvent.KEYCODE_DPAD_LEFT -> {
                if (!isMenuVisible) { viewModel.onMove(Direction.LEFT); true } else super.onKeyDown(keyCode, event)
            }
            KeyEvent.KEYCODE_DPAD_RIGHT -> {
                if (!isMenuVisible) { viewModel.onMove(Direction.RIGHT); true } else super.onKeyDown(keyCode, event)
            }
            else -> super.onKeyDown(keyCode, event)
        }
    }

    override fun onKeyLongPress(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            viewModel.restartGame()
            return true
        }
        return super.onKeyLongPress(keyCode, event)
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.isTracking && !event.isCanceled) {
            // Short press detected
            if (canUndo && !isMenuVisible) {
                viewModel.undo()
            }
            // Swallow the event to prevent exit even if we didn't undo
            return true
        }
        return super.onKeyUp(keyCode, event)
    }
}
