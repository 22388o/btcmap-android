package com.bubelov.coins.launcher

import com.bubelov.coins.repository.settings.SettingsRepository
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class LauncherViewModelTest {

    @Mock private lateinit var settingsRepository: SettingsRepository

    private lateinit var model: LauncherViewModel

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        model = LauncherViewModel(settingsRepository)
    }

    @Test
    fun getPermissionsExplained() {
        model.getPermissionsExplained()

        verify(settingsRepository).getBoolean(
            key = SettingsRepository.PERMISSIONS_EXPLAINED_KEY,
            defaultValue = false
        )

        verifyNoMoreInteractions(settingsRepository)
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