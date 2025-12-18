package com.myg.material2048.shared

import java.util.prefs.Preferences

class DesktopGameStorage : GameStorage {
    private val prefs: Preferences = Preferences.userNodeForPackage(DesktopGameStorage::class.java)

    override fun saveScore(score: Int) {
        prefs.putInt("score", score)
    }

    override fun getScore(): Int {
        return prefs.getInt("score", -1)
    }

    override fun saveBoard(boardStr: String) {
        prefs.put("board", boardStr)
    }

    override fun getBoard(): String? {
        return prefs.get("board", null)
    }
}
