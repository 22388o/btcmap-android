package com.bubelov.coins.repository

import com.bubelov.coins.TestSuite
import com.bubelov.coins.repository.place.PlacesRepository
import org.koin.core.inject

class PlacesRepositoryTests : TestSuite() {

    val repository: PlacesRepository by inject()

//    @Test
//    fun doNotUseAssetsCacheWhenEmpty() = runBlocking {
//        val builtInCache = declareMock<BuiltInPlacesCache>()
//        repository.find(UUID.randomUUID().toString())
//        verifyNoInteractions(builtInCache)
//        Unit
//    }
}