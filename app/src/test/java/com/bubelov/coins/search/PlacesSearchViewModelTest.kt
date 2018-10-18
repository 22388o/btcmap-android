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

package com.bubelov.coins.search

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.content.SharedPreferences
import android.content.res.Resources
import com.bubelov.coins.model.Place
import com.bubelov.coins.repository.place.PlacesRepository
import com.bubelov.coins.repository.placeicon.PlaceIconsRepository
import com.bubelov.coins.util.blockingObserve
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyZeroInteractions
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import java.util.*

class PlacesSearchViewModelTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock private lateinit var placesRepository: PlacesRepository
    @Mock private lateinit var placeIconsRepository: PlaceIconsRepository
    @Mock private lateinit var preferences: SharedPreferences
    @Mock private lateinit var resources: Resources
    private lateinit var model: PlacesSearchViewModel

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)

        model = PlacesSearchViewModel(
            placesRepository,
            placeIconsRepository,
            preferences,
            resources,
            Dispatchers.Default
        )

        model.setUp("BTC", null)

        runBlocking {
            whenever(placesRepository.findBySearchQuery(anyString()))
                .thenReturn(
                    listOf(
                        generatePlace("Bar 1", "BTC"),
                        generatePlace("Bar 2", "BTC"),
                        generatePlace("Bar 3", "LTC")
                    )
                )

            whenever(placeIconsRepository.getPlaceIcon(anyString()))
                .thenReturn(mock())
        }
    }

    @Test
    fun searchBars() = runBlocking {
        model.setQuery("bar")
        val rows = model.rows.blockingObserve()
        verify(placesRepository).findBySearchQuery("bar")
        Assert.assertEquals(2, rows.size)
        Assert.assertTrue(rows.all { it.name.contains("bar", ignoreCase = true) })
    }

    @Test
    fun emptyOnShortQuery() = runBlocking {
        model.setQuery("b")
        val results = model.rows.blockingObserve()
        verifyZeroInteractions(placesRepository)
        Assert.assertTrue(results.isEmpty())
    }

    @Test
    fun resetsLastSearch() = runBlocking {
        model.setQuery("bar")
        model.setQuery("")
        val results = model.rows.blockingObserve()
        Assert.assertTrue(results.isEmpty())
    }

    private fun generatePlace(name: String, currency: String): Place {
        return Place(
            id = name.hashCode().toLong(),
            name = name,
            latitude = 0.0,
            longitude = 0.0,
            description = "",
            category = "",
            currencies = arrayListOf(currency),
            openedClaims = 0,
            closedClaims = 0,
            phone = "",
            website = "",
            visible = true,
            openingHours = "",
            updatedAt = Date(0)
        )
    }
}