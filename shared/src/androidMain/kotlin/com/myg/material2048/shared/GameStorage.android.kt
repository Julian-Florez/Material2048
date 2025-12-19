package com.myg.material2048.shared

import android.content.Context
import android.content.SharedPreferences

class AndroidGameStorage(context: Context) : GameStorage {
    private val prefs: SharedPreferences = context.getSharedPreferences("game_prefs", Context.MODE_PRIVATE)

    override fun saveScore(score: Int) {
        prefs.edit().putInt("score", score).apply()
    }

    override fun getScore(): Int {
        return prefs.getInt("score", -1)
    }

    override fun saveBestScore(score: Int) {
        prefs.edit().putInt("best_score", score).apply()
    }

    override fun getBestScore(): Int {
        return prefs.getInt("best_score", 0)
    }

    override fun saveBoard(boardStr: String) {
        prefs.edit().putString("board", boardStr).apply()
    }

    override fun getBoard(): String? {
        return prefs.getString("board", null)
    }
}
