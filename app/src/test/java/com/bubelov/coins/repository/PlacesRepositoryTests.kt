package com.bubelov.coins.repository

import com.bubelov.coins.TestSuite
import com.bubelov.coins.repository.place.BuiltInPlacesCache
import com.bubelov.coins.repository.place.PlacesRepository
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.koin.core.inject
import org.koin.test.mock.declareMock
import java.util.*
import org.mockito.BDDMockito.*

class PlacesRepositoryTests : TestSuite() {

    val repository: PlacesRepository by inject()

    @Test
    fun doNotUseAssetsCacheWhenEmpty() = runBlocking {
        val builtInCache = declareMock<BuiltInPlacesCache>()
        repository.find(UUID.randomUUID().toString())
        verifyNoInteractions(builtInCache)
        Unit
    }
}