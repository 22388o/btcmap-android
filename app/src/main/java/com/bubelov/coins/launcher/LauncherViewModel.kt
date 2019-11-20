package com.bubelov.coins.launcher

import androidx.lifecycle.ViewModel
import com.bubelov.coins.repository.settings.SettingsRepository
import javax.inject.Inject

class LauncherViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {
    var permissionsExplained
        get() = settingsRepository.permissionsExplained
        set(value) {
            settingsRepository.permissionsExplained = value
        }
}