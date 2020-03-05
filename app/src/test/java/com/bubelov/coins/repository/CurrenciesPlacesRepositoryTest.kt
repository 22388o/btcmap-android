package com.bubelov.coins.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.bubelov.coins.Database
import com.bubelov.coins.api.coins.CoinsApi
import com.bubelov.coins.data.CurrencyPlace
import com.bubelov.coins.repository.currencyplace.BuiltInCurrenciesPlacesCache
import com.bubelov.coins.repository.currencyplace.CurrenciesPlacesRepository
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.runBlocking
import org.joda.time.DateTime
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import java.util.*

class CurrenciesPlacesRepositoryTest {
    @JvmField @Rule val instantExecutor = InstantTaskExecutorRule()

    @Mock private lateinit var api: CoinsApi
    @Mock private lateinit var db: Database
    @Mock private lateinit var builtInCache: BuiltInCurrenciesPlacesCache

    init {
        MockitoAnnotations.initMocks(this)
    }

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
    }

//    @Test
//    fun findCurrenciesPlacesByPlaceId_whenTableIsEmpty_usesBuiltInCache() = runBlocking {
//        whenever(db.count()).thenReturn(0)
//
//        val currenciesPlaces = listOf(
//            CurrencyPlace(
//                currencyId = UUID.randomUUID().toString(),
//                placeId = UUID.randomUUID().toString(),
//                createdAt = DateTime.now(),
//                updatedAt = DateTime.now()
//            )
//        )
//
//        whenever(builtInCache.getCurrenciesPlaces()).thenReturn(currenciesPlaces)
//        whenever(db.findByPlaceId(currenciesPlaces.first().placeId)).thenReturn(currenciesPlaces)
//
//        val repository = CurrenciesPlacesRepository(api, db, builtInCache)
//
//        assertEquals(currenciesPlaces, repository.findByPlaceId(currenciesPlaces.first().placeId))
//
//        verify(builtInCache).getCurrenciesPlaces()
//        verify(db).insert(currenciesPlaces)
//    }
}