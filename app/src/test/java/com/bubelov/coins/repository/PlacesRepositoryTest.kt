package com.bubelov.coins.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.bubelov.coins.Database
import com.bubelov.coins.api.coins.CoinsApi
import com.bubelov.coins.emptyPlace
import com.bubelov.coins.repository.place.BuiltInPlacesCache
import com.bubelov.coins.repository.place.PlacesRepository
import com.bubelov.coins.repository.user.UserRepository
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import java.util.*

class PlacesRepositoryTest {
    @JvmField @Rule val instantExecutor = InstantTaskExecutorRule()

    @Mock private lateinit var api: CoinsApi
    @Mock private lateinit var db: Database
    @Mock private lateinit var placesAssetsCache: BuiltInPlacesCache
    @Mock private lateinit var userRepository: UserRepository

    init {
        MockitoAnnotations.initMocks(this)
    }

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
    }

//    @Test
//    fun usesAssetsCacheWhenEmpty() = runBlocking {
//        whenever(placesDb.count()).thenReturn(0)
//        val places = listOf(emptyPlace().copy(id = UUID.randomUUID().toString(), name = "Cafe"))
//        whenever(placesAssetsCache.getPlaces()).thenReturn(places)
//
//        val repository = PlacesRepository(api, placesDb, placesAssetsCache, userRepository)
//
//        repository.find(UUID.randomUUID().toString())
//
//        verify(placesAssetsCache).getPlaces()
//        verify(placesDb).insert(places)
//    }
}