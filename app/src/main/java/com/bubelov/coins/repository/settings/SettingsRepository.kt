package com.bubelov.coins.repository.settings

import android.content.SharedPreferences
import androidx.core.content.edit

class SettingsRepository(
    private val preferences: SharedPreferences
) {

    fun getBoolean(key: String, defaultValue: Boolean) = preferences.getBoolean(key, defaultValue)

    fun setBoolean(key: String, value: Boolean) = preferences.edit {
        putBoolean(key, value)
    }

    companion object {
        const val PERMISSIONS_EXPLAINED_KEY = "permissions_explained"
    }
}