package com.bubelov.coins.permissions

import androidx.lifecycle.ViewModel
import com.bubelov.coins.repository.PreferencesRepository

class PermissionsViewModel(
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {

    suspend fun setPermissionsExplained(value: Boolean) = preferencesRepository.put(
        key = PreferencesRepository.PERMISSIONS_EXPLAINED_KEY,
        value = value.toString()
    )
}