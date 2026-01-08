package com.myg.material2048.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SentimentVeryDissatisfied
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.key.*
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
    
    // FocusRequester to ensure the game receives keyboard input
    val focusRequester = remember { FocusRequester() }

    // Request focus when the composable is first composed
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    BackHandler(enabled = uiState.canUndo) {
        gameViewModel.undo()
    }

    // Secondary handler
    BackHandler(enabled = !uiState.canUndo) {
        // Do nothing
    }

    // State to track if we've already handled a move in the current gesture
    var hasMovedInCurrentGesture by remember { mutableStateOf(false) }
    
    val gameModifier = Modifier
        .focusRequester(focusRequester)
        .focusable()
        .onKeyEvent { keyEvent ->
            if (keyEvent.type == KeyEventType.KeyDown) {
                when (keyEvent.key) {
                    Key.DirectionUp, Key.W -> {
                        gameViewModel.onMove(Direction.UP)
                        true
                    }
                    Key.DirectionDown, Key.S -> {
                        gameViewModel.onMove(Direction.DOWN)
                        true
                    }
                    Key.DirectionLeft, Key.A -> {
                        gameViewModel.onMove(Direction.LEFT)
                        true
                    }
                    Key.DirectionRight, Key.D -> {
                        gameViewModel.onMove(Direction.RIGHT)
                        true
                    }
                    Key.Z -> {
                        if (uiState.canUndo) {
                            gameViewModel.undo()
                        }
                        true
                    }
                    Key.R -> {
                        gameViewModel.restartGame()
                        true
                    }
                    else -> false
                }
            } else {
                false
            }
        }
        .pointerInput(Unit) {
            detectDragGestures(
                onDragStart = {
                    hasMovedInCurrentGesture = false
                },
                onDragEnd = {
                    hasMovedInCurrentGesture = false
                },
                onDragCancel = {
                    hasMovedInCurrentGesture = false
                },
                onDrag = { change, dragAmount ->
                    change.consume()
                    // Only process move if we haven't already moved in this gesture
                    if (!hasMovedInCurrentGesture) {
                        val (x, y) = dragAmount
                        // Threshold to prevent accidental small moves
                        val threshold = 20f
                        if (abs(x) > abs(y)) {
                            if (abs(x) > threshold) {
                                if (x > 0) gameViewModel.onMove(Direction.RIGHT) else gameViewModel.onMove(Direction.LEFT)
                                hasMovedInCurrentGesture = true
                            }
                        } else {
                            if (abs(y) > threshold) {
                                if (y > 0) gameViewModel.onMove(Direction.DOWN) else gameViewModel.onMove(Direction.UP)
                                hasMovedInCurrentGesture = true
                            }
                        }
                    }
                }
            )
        }

    Scaffold(
        // Moving the gesture detector to Scaffold so it covers the whole screen
        modifier = Modifier.then(gameModifier)
    ) { paddingValues ->
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
                         ScoreCard(score = uiState.score, bestScore = uiState.bestScore, isCompact = isCompact)
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
                    
                    Header(score = uiState.score, bestScore = uiState.bestScore, onUndo = { gameViewModel.undo() }, onRestart = { gameViewModel.restartGame() }, canUndo = uiState.canUndo)
                    
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
fun Header(score: Int, bestScore: Int, onUndo: () -> Unit, onRestart: () -> Unit, canUndo: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom 
    ) {
        ScoreCard(score = score, bestScore = bestScore)

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
fun ScoreCard(score: Int, bestScore: Int, isCompact: Boolean = false) {
    val backgroundColor = MaterialTheme.colorScheme.surfaceVariant
    val textColor = MaterialTheme.colorScheme.onSurfaceVariant
    val labelColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)

    Card(
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Column(
            modifier = Modifier.padding(
                horizontal = if (isCompact) 12.dp else 24.dp,
                vertical = if (isCompact) 8.dp else 12.dp
            ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Best Score Display
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = labelColor
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = bestScore.toString(),
                    style = MaterialTheme.typography.titleMedium,
                    color = labelColor,
                    fontWeight = FontWeight.Bold
                )
            }
            
            // Current Score Display
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

    // Modified to include focusable = false to prevent stealing keyboard focus on desktop
    Card(
        onClick = onClick,
        enabled = enabled,
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
