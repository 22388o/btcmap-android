package com.bubelov.coins.launcher

import com.bubelov.coins.TestSuite
import com.bubelov.coins.repository.PreferencesRepository
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.koin.core.inject
import org.koin.test.mock.declareMock
import org.mockito.BDDMockito.*

class LauncherViewModelTests : TestSuite() {

    val model: LauncherViewModel by inject()

    @Test
    fun getPermissionsExplained() = runBlocking {
        val preferencesRepository = declareMock<PreferencesRepository>()

        model.getPermissionsExplained()

        verify(preferencesRepository).get(PreferencesRepository.PERMISSIONS_EXPLAINED_KEY)
        verifyNoMoreInteractions(preferencesRepository)
    }

    @Test
    fun setPermissionsExplained() = runBlocking {
        val preferencesRepository = declareMock<PreferencesRepository>()

        val explained = true
        model.setPermissionsExplained(explained)

        verify(preferencesRepository).put(
            key = PreferencesRepository.PERMISSIONS_EXPLAINED_KEY,
            value = explained.toString()
        )
    }
}