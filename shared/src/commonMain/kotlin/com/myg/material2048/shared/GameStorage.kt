package com.myg.material2048.shared

interface GameStorage {
    fun saveScore(score: Int)
    fun getScore(): Int
    fun saveBestScore(score: Int)
    fun getBestScore(): Int
    fun saveBoard(boardStr: String)
    fun getBoard(): String?
}
