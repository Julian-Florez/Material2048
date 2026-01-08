package com.myg.material2048.shared

import kotlinx.browser.localStorage
import org.w3c.dom.get
import org.w3c.dom.set

class WebGameStorage : GameStorage {

    override fun saveScore(score: Int) {
        localStorage["material2048_score"] = score.toString()
    }

    override fun getScore(): Int {
        return localStorage["material2048_score"]?.toIntOrNull() ?: -1
    }

    override fun saveBestScore(score: Int) {
        localStorage["material2048_best_score"] = score.toString()
    }

    override fun getBestScore(): Int {
        return localStorage["material2048_best_score"]?.toIntOrNull() ?: 0
    }

    override fun saveBoard(boardStr: String) {
        localStorage["material2048_board"] = boardStr
    }

    override fun getBoard(): String? {
        return localStorage["material2048_board"]
    }
}

