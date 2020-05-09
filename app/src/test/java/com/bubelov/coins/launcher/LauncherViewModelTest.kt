package com.bubelov.coins.launcher

import com.bubelov.coins.repository.PreferencesRepository
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class LauncherViewModelTest {

    @Mock
    private lateinit var preferencesRepository: PreferencesRepository

    private lateinit var model: LauncherViewModel

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        model = LauncherViewModel(preferencesRepository)
    }

    @Test
    fun getPermissionsExplained() = runBlocking {
        model.getPermissionsExplained()

        verify(preferencesRepository).get(
            key = PreferencesRepository.PERMISSIONS_EXPLAINED_KEY
        )

        verifyNoMoreInteractions(preferencesRepository)
    }

    @Test
    fun setPermissionsExplained() = runBlocking {
        val explained = true
        model.setPermissionsExplained(explained)

        verify(preferencesRepository).put(
            key = PreferencesRepository.PERMISSIONS_EXPLAINED_KEY,
            value = explained.toString()
        )
    }
}