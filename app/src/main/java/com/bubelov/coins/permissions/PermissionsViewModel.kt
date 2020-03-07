package com.bubelov.coins.permissions

import androidx.lifecycle.ViewModel
import com.bubelov.coins.repository.settings.SettingsRepository

class PermissionsViewModel(
    private val settingsRepository: SettingsRepository
) : ViewModel() {
    var permissionsExplained
        get() = settingsRepository.permissionsExplained
        set(value) {
            settingsRepository.permissionsExplained = value
        }
}