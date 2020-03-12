package com.bubelov.coins.permissions

import com.bubelov.coins.repository.PreferencesRepository
import com.nhaarman.mockitokotlin2.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

@ExperimentalCoroutinesApi
class PermissionsViewModelTest {

    @Mock private lateinit var preferencesRepository: PreferencesRepository

    private lateinit var model: PermissionsViewModel

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        model = PermissionsViewModel(preferencesRepository)
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