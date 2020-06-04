package com.bubelov.coins.permissions

import com.bubelov.coins.TestSuite
import com.bubelov.coins.repository.PreferencesRepository
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.koin.core.inject
import org.koin.test.mock.declareMock
import org.mockito.BDDMockito.*

class PermissionsViewModelTests : TestSuite() {

    val model: PermissionsViewModel by inject()

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