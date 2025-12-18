package com.myg.material2048.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SentimentVeryDissatisfied
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import com.myg.material2048.model.Direction
import com.myg.material2048.viewmodel.GameViewModel
import kotlin.math.abs

// BackHandler stub for non-Android platforms or proper expect/actual
@Composable
expect fun BackHandler(enabled: Boolean = true, onBack: () -> Unit)

@Composable
fun GameScreen(gameViewModel: GameViewModel) {
    val uiState by gameViewModel.uiState.collectAsState()
    
    BackHandler(enabled = uiState.canUndo) {
        gameViewModel.undo()
    }

    // Secondary handler
    BackHandler(enabled = !uiState.canUndo) {
        // Do nothing
    }

    var swipeDirection by remember { mutableStateOf<Direction?>(null) }
    
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
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            val isLandscape = maxWidth > maxHeight
            
            if (isLandscape) {
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
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Spacer(modifier = Modifier.weight(1f))
                    
                    Header(score = uiState.score, onUndo = { gameViewModel.undo() }, onRestart = { gameViewModel.restartGame() }, canUndo = uiState.canUndo)
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    BoxWithConstraints(
                         modifier = Modifier.fillMaxWidth(),
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
    val backgroundColor = MaterialTheme.colorScheme.surfaceVariant
    val textColor = MaterialTheme.colorScheme.onSurfaceVariant

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
    val backgroundColor = MaterialTheme.colorScheme.surfaceVariant
    val iconColor = MaterialTheme.colorScheme.onSurfaceVariant

    Card(
        onClick = onClick,
        enabled = enabled,
        // modifier = Modifier.focusProperties { canFocus = false }, // Not available in commonMain compose 1.6.0? Check versions. Assuming OK.
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
