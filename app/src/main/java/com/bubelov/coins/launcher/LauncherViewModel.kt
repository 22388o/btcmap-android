package com.bubelov.coins.launcher

import androidx.lifecycle.ViewModel
import com.bubelov.coins.repository.settings.SettingsRepository

class LauncherViewModel(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    fun getPermissionsExplained() = settingsRepository.getBoolean(
        key = SettingsRepository.PERMISSIONS_EXPLAINED_KEY,
        defaultValue = false
    )

    fun setPermissionsExplained(value: Boolean) = settingsRepository.setBoolean(
        key = SettingsRepository.PERMISSIONS_EXPLAINED_KEY,
        value = value
    )
}