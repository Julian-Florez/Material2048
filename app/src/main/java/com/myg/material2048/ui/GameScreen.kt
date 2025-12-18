package com.myg.material2048.ui

import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SentimentVeryDissatisfied
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.lifecycle.viewmodel.compose.viewModel
import com.myg.material2048.model.Direction
import com.myg.material2048.viewmodel.GameViewModel
import kotlin.math.abs

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreen(gameViewModel: GameViewModel = viewModel()) {
    val uiState by gameViewModel.uiState.collectAsState()
    
    // Primary handler for Undo
    BackHandler(enabled = uiState.canUndo) {
        gameViewModel.undo()
    }

    // Secondary handler to prevent exit on Back press when Undo is not available
    BackHandler(enabled = !uiState.canUndo) {
        // Do nothing, just consume the back press event
    }

    var swipeDirection by remember { mutableStateOf<Direction?>(null) }
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    
    val gameModifier = Modifier.pointerInput(Unit) {
        detectDragGestures(
            onDragEnd = {
                swipeDirection?.let {
                    gameViewModel.onMove(it)
                    swipeDirection = null
                }
            },
            onDrag = { change, dragAmount ->
                change.consume()
                val (x, y) = dragAmount
                if (abs(x) > abs(y)) {
                    if (x > 0) swipeDirection = Direction.RIGHT else if (x < 0) swipeDirection = Direction.LEFT
                } else {
                    if (y > 0) swipeDirection = Direction.DOWN else if (y < 0) swipeDirection = Direction.UP
                }
            }
        )
    }

    Scaffold { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            if (isLandscape) {
                BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                    val isCompact = maxWidth < 600.dp
                    val panelWidth = if (isCompact) 130.dp else 200.dp
                    val spacing = if (isCompact) 12.dp else 24.dp
                    
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxHeight()
                                .width(panelWidth),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                             ScoreCard(score = uiState.score, isCompact = isCompact)
                             Spacer(modifier = Modifier.height(spacing))
                             Row {
                                ActionButton(
                                    onClick = { gameViewModel.undo() },
                                    enabled = uiState.canUndo,
                                    icon = Icons.AutoMirrored.Filled.Undo,
                                    contentDescription = "Deshacer"
                                )
                                Spacer(Modifier.width(8.dp))
                                ActionButton(
                                    onClick = { gameViewModel.restartGame() },
                                    icon = Icons.Default.Refresh,
                                    contentDescription = "Reiniciar"
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.width(spacing))
                        
                        BoxWithConstraints(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight(),
                            contentAlignment = Alignment.Center
                        ) {
                            val boardSize = min(maxWidth, maxHeight)
                            
                            Box(
                                modifier = Modifier
                                    .size(boardSize)
                                    .aspectRatio(1f)
                                    .then(gameModifier)
                            ) {
                                GameBoardWithOverlay(uiState, gameViewModel, boardSize)
                            }
                        }
                    }
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center // Cambiado a Bottom para pegar más al tablero, pero Center funciona mejor con Spacer ponderado
                ) {
                    // Spacer superior empuja el contenido hacia abajo
                    Spacer(modifier = Modifier.weight(1f))
                    
                    Header(score = uiState.score, onUndo = { gameViewModel.undo() }, onRestart = { gameViewModel.restartGame() }, canUndo = uiState.canUndo)
                    
                    Spacer(modifier = Modifier.height(16.dp)) // Espacio fijo entre header y tablero
                    
                    BoxWithConstraints(
                         modifier = Modifier
                            .fillMaxWidth(),
                         contentAlignment = Alignment.Center
                    ) {
                        val boardSize = min(maxWidth, maxHeight)
                        Box(
                            modifier = Modifier
                                .size(boardSize)
                                .aspectRatio(1f)
                                .then(gameModifier)
                        ) {
                            GameBoardWithOverlay(uiState, gameViewModel, boardSize)
                        }
                    }
                    
                    // Spacer inferior empuja el contenido hacia arriba, balanceando el superior
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
fun GameBoardWithOverlay(uiState: com.myg.material2048.model.GameUiState, gameViewModel: GameViewModel, boardSize: Dp) {
    GameBoard(board = uiState.board)
    val cornerRadius = boardSize * 0.05f

    if (uiState.isGameOver) {
        GameOverOverlay(onRestart = { gameViewModel.restartGame() }, cornerRadius = cornerRadius)
    } else if (uiState.hasWon && !uiState.keepPlaying) {
        GameWonOverlay(
            onRestart = { gameViewModel.restartGame() },
            onKeepPlaying = { gameViewModel.keepPlaying() },
            cornerRadius = cornerRadius
        )
    }
}

// Kept purely for backward compatibility if needed, but GameBoardWithOverlay is preferred
@Composable
fun GameContent(uiState: com.myg.material2048.model.GameUiState, gameViewModel: GameViewModel) {
    // This is a simplified fallback that might not match corners perfectly if used directly without size
    GameBoard(board = uiState.board)
}

@Composable
fun Header(score: Int, onUndo: () -> Unit, onRestart: () -> Unit, canUndo: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom 
    ) {
        ScoreCard(score = score)

        Row {
            ActionButton(
                onClick = onUndo,
                enabled = canUndo,
                icon = Icons.AutoMirrored.Filled.Undo,
                contentDescription = "Deshacer"
            )
            Spacer(Modifier.width(8.dp))
            ActionButton(
                onClick = onRestart,
                icon = Icons.Default.Refresh,
                contentDescription = "Reiniciar"
            )
        }
    }
}

@Composable
fun ScoreCard(score: Int, isCompact: Boolean = false) {
    val context = LocalContext.current
    val neutralColorId = context.resources.getIdentifier("system_neutral2_700", "color", "android")
    val backgroundColor = if (neutralColorId != 0) {
        Color(context.resources.getColor(neutralColorId, context.theme))
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }

    val neutralTextColorId = context.resources.getIdentifier("system_neutral2_50", "color", "android")
    val textColor = if (neutralTextColorId != 0) {
        Color(context.resources.getColor(neutralTextColorId, context.theme))
    } else {
        MaterialTheme.colorScheme.onSurface
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Column(
            modifier = Modifier.padding(
                horizontal = if (isCompact) 12.dp else 24.dp,
                vertical = if (isCompact) 8.dp else 16.dp
            ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = score.toString(), 
                style = if (isCompact) MaterialTheme.typography.headlineMedium else MaterialTheme.typography.displaySmall, 
                fontWeight = FontWeight.Bold,
                color = textColor
            )
        }
    }
}

@Composable
fun ActionButton(
    onClick: () -> Unit,
    icon: ImageVector,
    contentDescription: String,
    enabled: Boolean = true
) {
    val context = LocalContext.current
    val neutralColorId = context.resources.getIdentifier("system_neutral2_700", "color", "android")
    val backgroundColor = if (neutralColorId != 0) {
        Color(context.resources.getColor(neutralColorId, context.theme))
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }

    val neutralIconColorId = context.resources.getIdentifier("system_neutral2_50", "color", "android")
    val iconColor = if (neutralIconColorId != 0) {
        Color(context.resources.getColor(neutralIconColorId, context.theme))
    } else {
        MaterialTheme.colorScheme.onSurface
    }

    Card(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier.focusProperties { canFocus = false },
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Box(
            modifier = Modifier.padding(12.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = iconColor
            )
        }
    }
}

@Composable
fun GameOverOverlay(onRestart: () -> Unit, cornerRadius: Dp = 0.dp) {
    Overlay(
        icon = Icons.Default.SentimentVeryDissatisfied,
        buttonIcon = Icons.Default.Refresh,
        onClick = onRestart,
        cornerRadius = cornerRadius
    )
}

@Composable
fun GameWonOverlay(onRestart: () -> Unit, onKeepPlaying: () -> Unit, cornerRadius: Dp = 0.dp) {
     Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(cornerRadius))
            .background(MaterialTheme.colorScheme.scrim.copy(alpha = 0.5f)),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.padding(32.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.EmojiEvents,
                    contentDescription = "Victoria",
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                     Button(onClick = onKeepPlaying) {
                        Icon(imageVector = Icons.Default.PlayArrow, contentDescription = "Seguir jugando")
                    }
                    Button(onClick = onRestart) {
                        Icon(imageVector = Icons.Default.Refresh, contentDescription = "Nuevo juego")
                    }
                }
            }
        }
    }
}

@Composable
private fun Overlay(icon: ImageVector, buttonIcon: ImageVector, onClick: () -> Unit, cornerRadius: Dp) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(cornerRadius))
            .background(MaterialTheme.colorScheme.scrim.copy(alpha = 0.5f)),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.padding(32.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.error
                )
                Button(onClick = onClick) {
                    Icon(imageVector = buttonIcon, contentDescription = null)
                }
            }
        }
    }
}
