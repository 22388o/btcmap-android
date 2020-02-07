package com.bubelov.coins.repository.settings

import android.content.SharedPreferences
import androidx.core.content.edit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepository @Inject constructor(
    private val preferences: SharedPreferences
) {
    var permissionsExplained
        get() = preferences.getBoolean(PERMISSIONS_EXPLAINED_KEY, false)
        set(value) = preferences.edit { putBoolean(PERMISSIONS_EXPLAINED_KEY, value) }

    companion object {
        const val PERMISSIONS_EXPLAINED_KEY = "permissions_explained"
    }
}