package com.odencave.models

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Preferences
import com.badlogic.gdx.utils.Json

object Leaderboard {
    private val folder = "Zenith"
    private val prefsName = folder + "/leaderboard"
    private val key = "key"

    private val leaderboardFile: Preferences by lazy { Gdx.app.getPreferences(prefsName) }

    private val json by lazy { Json() }

    private val fakeList = listOf(
        LeaderboardEntry("GKF", 900),
        LeaderboardEntry("MML", 100),
        LeaderboardEntry("HER", 450),
    )

    fun getLeaderboardEntries(): List<LeaderboardEntry> {
        val textJson = leaderboardFile.getString(key, "")
        val entries = if (textJson.isNullOrBlank()) {
            fakeList
        } else {
            json.fromJson(ArrayList(listOf<LeaderboardEntry>())::class.java, textJson)
        }
        return entries.sortedByDescending { it.score }.take(3)
    }

    fun addEntry(entry: LeaderboardEntry) {
        val allEntries = ArrayList(getLeaderboardEntries()).apply {
            add(entry)
        }
        saveLeaderboard(allEntries)
    }

    private fun saveLeaderboard(list: List<LeaderboardEntry>) {
        leaderboardFile.putString(key, json.toJson(list)).flush()
    }


}
