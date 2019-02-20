/*
 * This is free and unencumbered software released into the public domain.
 *
 * Anyone is free to copy, modify, publish, use, compile, sell, or
 * distribute this software, either in source code form or as a compiled
 * binary, for any purpose, commercial or non-commercial, and by any
 * means.
 *
 * In jurisdictions that recognize copyright laws, the author or authors
 * of this software dedicate any and all copyright interest in the
 * software to the public domain. We make this dedication for the benefit
 * of the public at large and to the detriment of our heirs and
 * successors. We intend this dedication to be an overt act of
 * relinquishment in perpetuity of all present and future rights to this
 * software under copyright law.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS BE LIABLE FOR ANY CLAIM, DAMAGES OR
 * OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 *
 * For more information, please refer to <https://unlicense.org>
 */

package com.bubelov.coins.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.bubelov.coins.api.coins.CoinsApi
import com.bubelov.coins.model.CurrencyPlace
import com.bubelov.coins.repository.currencyplace.BuiltInCurrenciesPlacesCache
import com.bubelov.coins.repository.currencyplace.CurrenciesPlacesDb
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
    @Mock private lateinit var db: CurrenciesPlacesDb
    @Mock private lateinit var builtInCache: BuiltInCurrenciesPlacesCache

    init {
        MockitoAnnotations.initMocks(this)
    }

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
    }

    @Test
    fun findCurrenciesPlacesByPlaceId_whenTableIsEmpty_usesBuiltInCache() = runBlocking {
        whenever(db.count()).thenReturn(0)

        val currenciesPlaces = listOf(
            CurrencyPlace(
                currencyId = UUID.randomUUID().toString(),
                placeId = UUID.randomUUID().toString(),
                createdAt = DateTime.now(),
                updatedAt = DateTime.now()
            )
        )

        whenever(builtInCache.getCurrenciesPlaces()).thenReturn(currenciesPlaces)
        whenever(db.findByPlaceId(currenciesPlaces.first().placeId)).thenReturn(currenciesPlaces)

        val repository = CurrenciesPlacesRepository(api, db, builtInCache)

        assertEquals(currenciesPlaces, repository.findByPlaceId(currenciesPlaces.first().placeId))

        verify(builtInCache).getCurrenciesPlaces()
        verify(db).insert(currenciesPlaces)
    }
}