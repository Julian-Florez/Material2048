package com.myg.material2048.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.myg.material2048.model.Direction
import com.myg.material2048.model.GameUiState
import com.myg.material2048.model.Tile
import com.myg.material2048.shared.GameStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random

const val GRID_SIZE = 4

class GameViewModel(private val storage: GameStorage) : ViewModel() {

    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    private var lastValidUndoBoard: List<List<Tile?>>? = null
    private var lastValidUndoScore: Int = 0

    init {
        loadGame()
    }

    private fun loadGame() {
        val savedScore = storage.getScore()
        val savedBoardStr = storage.getBoard()

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
        storage.saveScore(_uiState.value.score)
        storage.saveBoard(serializeBoard(_uiState.value.board))
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

            val cleanLine = line.map { it.copy(isFromUndo = false) }

            if (cleanLine.isEmpty()) continue

            val newLine = mutableListOf<Tile>()
            var j = 0
            while (j < cleanLine.size) {
                val currentTile = cleanLine[j]
                if (j + 1 < cleanLine.size && cleanLine[j + 1].value == currentTile.value) {
                    val otherTile = cleanLine[j+1]
                    val mergedValue = currentTile.value * 2
                    
                    val pos1 = originalPositions[currentTile.id]
                    val pos2 = originalPositions[otherTile.id]
                    
                    val t1 = currentTile.copy(previousPosition = pos1)
                    val t2 = otherTile.copy(previousPosition = pos2)
                    
                    val mergedTile = Tile(
                        value = mergedValue,
                        mergedFrom = listOf(t1, t2)
                    )
                    
                    newLine.add(mergedTile)
                    points += mergedTile.value
                    j += 2
                } else {
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
