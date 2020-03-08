package com.bubelov.coins.permissions

import com.bubelov.coins.repository.settings.SettingsRepository
import com.nhaarman.mockitokotlin2.verify
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class PermissionsViewModelTest {

    @Mock private lateinit var settingsRepository: SettingsRepository

    private lateinit var model: PermissionsViewModel

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        model = PermissionsViewModel(settingsRepository)
    }

    @Test
    fun setPermissionsExplained() {
        val explained = true
        model.setPermissionsExplained(explained)

        verify(settingsRepository).setBoolean(
            key = SettingsRepository.PERMISSIONS_EXPLAINED_KEY,
            value = explained
        )
    }
}