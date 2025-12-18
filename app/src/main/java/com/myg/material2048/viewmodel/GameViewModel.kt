package com.myg.material2048.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.myg.material2048.model.Direction
import com.myg.material2048.model.GameUiState
import com.myg.material2048.model.Tile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random
import kotlin.math.abs

const val GRID_SIZE = 4

class GameViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    private var lastValidUndoBoard: List<List<Tile?>>? = null
    private var lastValidUndoScore: Int = 0
    
    private val prefs = application.getSharedPreferences("game_prefs", Context.MODE_PRIVATE)

    init {
        loadGame()
    }

    private fun loadGame() {
        val savedScore = prefs.getInt("score", -1)
        val savedBoardStr = prefs.getString("board", null)

        if (savedScore != -1 && savedBoardStr != null) {
            val board = deserializeBoard(savedBoardStr)
            _uiState.value = GameUiState(
                board = board,
                score = savedScore,
                isGameOver = isGameOver(board),
                canUndo = false
            )
            lastValidUndoBoard = null
            lastValidUndoScore = 0
        } else {
            restartGame()
        }
    }

    private fun saveGame() {
        val editor = prefs.edit()
        editor.putInt("score", _uiState.value.score)
        editor.putString("board", serializeBoard(_uiState.value.board))
        editor.apply()
    }
    
    private fun serializeBoard(board: List<List<Tile?>>): String {
        return board.flatten().joinToString(",") { it?.value?.toString() ?: "0" }
    }

    private fun deserializeBoard(str: String): List<List<Tile?>> {
        val values = str.split(",").mapNotNull { it.toIntOrNull() }
        val board = createEmptyBoard()
        if (values.size == GRID_SIZE * GRID_SIZE) {
            for (r in 0 until GRID_SIZE) {
                for (c in 0 until GRID_SIZE) {
                    val valAt = values[r * GRID_SIZE + c]
                    if (valAt > 0) {
                        board[r][c] = Tile(valAt)
                    } else {
                        board[r][c] = null
                    }
                }
            }
        } else {
            // Fallback if data is corrupted
            return addRandomTile(addRandomTile(createEmptyBoard()))
        }
        return board
    }

    fun restartGame() {
        viewModelScope.launch {
            var board: List<List<Tile?>> = List(GRID_SIZE) { List(GRID_SIZE) { null } }
            board = addRandomTile(board)
            board = addRandomTile(board)

            _uiState.value = GameUiState(board = board, score = 0, isGameOver = false, hasWon = false, keepPlaying = false, canUndo = false)
            lastValidUndoBoard = null
            lastValidUndoScore = 0
            saveGame()
        }
    }

    fun keepPlaying() {
        _uiState.update { it.copy(keepPlaying = true) }
        saveGame()
    }

    fun onMove(direction: Direction) {
        if (_uiState.value.isGameOver) return

        viewModelScope.launch {
            val currentBoard = _uiState.value.board
            val result = moveAndMerge(currentBoard, direction)

            if (result.moved) {
                lastValidUndoBoard = cleanBoardForUndo(currentBoard)
                lastValidUndoScore = _uiState.value.score

                val newBoard = addRandomTile(result.board)
                val newScore = _uiState.value.score + result.points
                
                val reached2048 = newBoard.any { row -> row.any { it?.value == 2048 } }
                val currentlyWon = _uiState.value.hasWon
                val hasWonNow = !currentlyWon && reached2048

                val isGameOver = isGameOver(newBoard)

                _uiState.update {
                    it.copy(
                        board = newBoard,
                        score = newScore,
                        isGameOver = isGameOver,
                        hasWon = currentlyWon || hasWonNow,
                        canUndo = true
                    )
                }
                saveGame()
            }
        }
    }

    fun undo() {
        viewModelScope.launch {
            val boardToRestore = lastValidUndoBoard
            if (boardToRestore != null) {
                val currentTileIds = _uiState.value.board.flatten().filterNotNull().map { it.id }.toSet()
                
                val restoredBoard = boardToRestore.map { row ->
                    row.map { tile ->
                        if (tile != null) {
                            val wasMerged = !currentTileIds.contains(tile.id)
                            
                            tile.copy(
                                previousPosition = null, 
                                mergedFrom = null,
                                isFromUndo = wasMerged 
                            )
                        } else null
                    }
                }

                _uiState.update {
                    it.copy(
                        board = restoredBoard,
                        score = lastValidUndoScore,
                        isGameOver = false,
                        canUndo = false
                    )
                }
                lastValidUndoBoard = null
                saveGame()
            }
        }
    }
    
    private fun cleanBoardForUndo(board: List<List<Tile?>>): List<List<Tile?>> {
        return board.map { row ->
            row.map { tile ->
                // Es crucial limpiar 'isFromUndo' para que al restaurar este tablero
                // y luego hacer una jugada normal, las fichas no piensen que vienen de un undo.
                // También limpiamos mergedFrom y previousPosition para tener un estado "limpio".
                tile?.copy(previousPosition = null, mergedFrom = null, isFromUndo = false)
            }
        }
    }

    private fun createEmptyBoard(): MutableList<MutableList<Tile?>> {
        return MutableList(GRID_SIZE) { MutableList(GRID_SIZE) { null } }
    }

    private fun addRandomTile(board: List<List<Tile?>>): List<List<Tile?>> {
        val emptyCells = mutableListOf<Pair<Int, Int>>()
        board.forEachIndexed { r, row ->
            row.forEachIndexed { c, tile ->
                if (tile == null) {
                    emptyCells.add(r to c)
                }
            }
        }

        if (emptyCells.isNotEmpty()) {
            val (r, c) = emptyCells.random()
            val newValue = if (Random.nextFloat() < 0.9f) 2 else 4
            val newBoard = board.map { it.toMutableList() }.toMutableList()
            newBoard[r][c] = Tile(newValue)
            return newBoard
        }
        return board
    }

    private data class MoveResult(val board: List<List<Tile?>>, val moved: Boolean, val points: Int)

    private fun moveAndMerge(board: List<List<Tile?>>, direction: Direction): MoveResult {
        var points = 0
        var moved = false

        val originalPositions = board.flatMapIndexed { r, row ->
            row.mapIndexedNotNull { c, tile -> tile?.let { it.id to (r to c) } }
        }.toMap()

        val newBoard = createEmptyBoard()

        for (i in 0 until GRID_SIZE) {
            val lineIndices = when (direction) {
                Direction.UP -> (0 until GRID_SIZE).map { r -> r to i }
                Direction.DOWN -> (0 until GRID_SIZE).reversed().map { r -> r to i }
                Direction.LEFT -> (0 until GRID_SIZE).map { c -> i to c }
                Direction.RIGHT -> (0 until GRID_SIZE).reversed().map { c -> i to c }
            }
            val line = lineIndices.map { (r, c) -> board[r][c] }.filterNotNull()

            // Filtramos las fichas válidas y limpiamos estados residuales como isFromUndo
            // para que no interfieran en la lógica de fusión o movimiento actual.
            val cleanLine = line.map { it.copy(isFromUndo = false) }

            if (cleanLine.isEmpty()) continue

            val newLine = mutableListOf<Tile>()
            var j = 0
            while (j < cleanLine.size) {
                val currentTile = cleanLine[j]
                if (j + 1 < cleanLine.size && cleanLine[j + 1].value == currentTile.value) {
                    // Merge
                    val otherTile = cleanLine[j+1]
                    val mergedValue = currentTile.value * 2
                    
                    val pos1 = originalPositions[currentTile.id]
                    val pos2 = originalPositions[otherTile.id]
                    
                    // Actualizamos las fichas que se guardarán en mergedFrom para que tengan su posición
                    // original correcta como previousPosition, asegurando que la animación comience desde allí.
                    val t1 = currentTile.copy(previousPosition = pos1)
                    val t2 = otherTile.copy(previousPosition = pos2)
                    
                    // IMPORTANT: We must generate a NEW ID for the merged tile to ensure it's treated as a new entity
                    // and triggers the LaunchedEffect correctly in the UI.
                    val mergedTile = Tile(
                        value = mergedValue,
                        mergedFrom = listOf(t1, t2)
                    )
                    
                    newLine.add(mergedTile)
                    points += mergedTile.value
                    j += 2
                } else {
                    // Si la ficha no se fusiona, limpiamos su historial de fusión.
                    newLine.add(currentTile.copy(mergedFrom = null))
                    j += 1
                }
            }

            for (k in 0 until newLine.size) {
                val tile = newLine[k]
                val (r, c) = when (direction) {
                    Direction.UP -> k to i
                    Direction.DOWN -> (GRID_SIZE - 1 - k) to i
                    Direction.LEFT -> i to k
                    Direction.RIGHT -> i to (GRID_SIZE - 1 - k)
                }
                
                // If it is a merged tile (newly created above), it won't have an ID in originalPositions.
                // If it is an existing tile, we keep its previous position for movement animation.
                val originalPos = originalPositions[tile.id] 
                
                newBoard[r][c] = tile.copy(previousPosition = originalPos)

                if (originalPos == null || originalPos.first != r || originalPos.second != c) {
                    moved = true
                }
            }
        }
        if (!moved) {
            val oldTileCount = board.sumOf { row -> row.count { it != null } }
            val newTileCount = newBoard.sumOf { row -> row.count { it != null } }
            if (oldTileCount != newTileCount) {
                moved = true
            }
        }

        return MoveResult(newBoard, moved, points)
    }

    private fun isGameOver(board: List<List<Tile?>>): Boolean {
        if (board.any { row -> row.any { it == null } }) return false

        for (r in 0 until GRID_SIZE) {
            for (c in 0 until GRID_SIZE) {
                val value = board[r][c]?.value ?: continue
                if (c + 1 < GRID_SIZE && board[r][c + 1]?.value == value) return false
                if (r + 1 < GRID_SIZE && board[r + 1][c]?.value == value) return false
            }
        }
        return true
    }
}
