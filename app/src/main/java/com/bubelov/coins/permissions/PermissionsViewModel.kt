package com.bubelov.coins.permissions

import androidx.lifecycle.ViewModel
import com.bubelov.coins.repository.settings.SettingsRepository

class PermissionsViewModel(
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