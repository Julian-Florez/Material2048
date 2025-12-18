package com.myg.material2048.model

import androidx.compose.runtime.Stable
import java.util.concurrent.atomic.AtomicInteger

// Un contador para dar un ID único a cada ficha, esencial para las animaciones.
private val tileIdCounter = AtomicInteger(0)

/**
 * Representa una ficha en el tablero.
 * @param value El valor numérico de la ficha (2, 4, 8...).
 * @param id Un identificador único para ayudar a Compose a diferenciar fichas.
 * @param previousPosition La posición anterior de la ficha, para animar el movimiento.
 * @param mergedFrom Lista de fichas que se fusionaron para crear esta ficha.
 * @param isFromUndo Indica si esta ficha fue restaurada por un "deshacer" y debe animarse de forma diferente (aparecer en lugar de moverse).
 */
@Stable
data class Tile(
    val value: Int,
    val id: Int = tileIdCounter.incrementAndGet(),
    val previousPosition: Pair<Int, Int>? = null,
    val mergedFrom: List<Tile>? = null,
    val isFromUndo: Boolean = false
)

/**
 * Representa el estado completo de la UI en un momento dado.
 * Es inmutable, por lo que cada cambio creará una nueva instancia.
 */
@Stable
data class GameUiState(
    val board: List<List<Tile?>> = emptyList(),
    val score: Int = 0,
    val bestScore: Int = 0, // Puedes implementarlo con SharedPreferences
    val isGameOver: Boolean = false,
    val hasWon: Boolean = false,
    val keepPlaying: Boolean = false, // Indica si el usuario decidió continuar tras ganar
    val canUndo: Boolean = false // Nuevo campo para controlar el botón de deshacer
)
