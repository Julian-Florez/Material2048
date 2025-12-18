package com.myg.material2048.model

import androidx.compose.runtime.Stable

// Simple counter for ID generation. 
// In a highly concurrent environment, consider using atomic primitives or Mutex.
private var tileIdCounter = 0

/**
 * Representa una ficha en el tablero.
 */
@Stable
data class Tile(
    val value: Int,
    val id: Int = ++tileIdCounter,
    val previousPosition: Pair<Int, Int>? = null,
    val mergedFrom: List<Tile>? = null,
    val isFromUndo: Boolean = false
)

/**
 * Representa el estado completo de la UI en un momento dado.
 */
@Stable
data class GameUiState(
    val board: List<List<Tile?>> = emptyList(),
    val score: Int = 0,
    val bestScore: Int = 0,
    val isGameOver: Boolean = false,
    val hasWon: Boolean = false,
    val keepPlaying: Boolean = false,
    val canUndo: Boolean = false
)
