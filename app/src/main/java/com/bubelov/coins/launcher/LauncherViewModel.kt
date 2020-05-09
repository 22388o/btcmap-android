package com.bubelov.coins.launcher

import androidx.lifecycle.ViewModel
import com.bubelov.coins.repository.PreferencesRepository
import kotlinx.coroutines.flow.map

class LauncherViewModel(
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {

    fun getPermissionsExplained() = preferencesRepository.get(
        key = PreferencesRepository.PERMISSIONS_EXPLAINED_KEY
    ).map { it.toBoolean() }

    suspend fun setPermissionsExplained(value: Boolean) = preferencesRepository.put(
        key = PreferencesRepository.PERMISSIONS_EXPLAINED_KEY,
        value = value.toString()
    )
}