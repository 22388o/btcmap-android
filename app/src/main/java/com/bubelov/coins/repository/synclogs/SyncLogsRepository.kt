package com.bubelov.coins.repository.synclogs

import android.content.SharedPreferences
import com.bubelov.coins.model.SyncLogEntry
import com.google.gson.Gson

class SyncLogsRepository(
    private val preferences: SharedPreferences,
    private val gson: Gson
) {
    fun all(): List<SyncLogEntry> {
        return when (val json = preferences.getString(SYNC_LOGS_KEY, "")) {
            "" -> emptyList()
            else -> gson.fromJson(json, SyncLog::class.java).entries
        }
    }

    fun insert(entry: SyncLogEntry) {
        val entries = mutableListOf<SyncLogEntry>()
        entries += all()
        entries += entry

        preferences.edit().putString(
            SYNC_LOGS_KEY,
            gson.toJson(SyncLog(entries))
        ).apply()
    }

    data class SyncLog(val entries: List<SyncLogEntry>)

    companion object {
        private const val SYNC_LOGS_KEY = "sync_logs"
    }
}