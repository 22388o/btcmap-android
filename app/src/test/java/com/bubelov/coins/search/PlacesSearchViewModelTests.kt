package com.bubelov.coins.search

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.bubelov.coins.TestSuite
import com.bubelov.coins.data.Place
import com.bubelov.coins.repository.place.PlacesRepository
import com.bubelov.coins.util.blockingObserve
import kotlinx.coroutines.runBlocking
import org.joda.time.DateTime
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.koin.core.inject
import org.koin.test.mock.declareMock
import java.util.*
import org.mockito.BDDMockito.*

class PlacesSearchViewModelTests : TestSuite() {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    val model: PlacesSearchViewModel by inject()

//    @Test
//    fun searchBars() = runBlocking {
//        val placesRepository = declareMock<PlacesRepository> {
//            given(findBySearchQuery(anyString()))
//                .willReturn(
//                    listOf(
//                        generatePlace("Bar 1"),
//                        generatePlace("Bar 2"),
//                        generatePlace("Bar 3")
//                    )
//                )
//        }
//
//        declareMock<PlaceIconsRepository> {
//            given(getPlaceIcon(anyString()))
//                .willReturn(declareMock())
//        }
//
//        model.setQuery("bar")
//        val rows = model.rows.blockingObserve()
//        verify(placesRepository).findBySearchQuery("bar")
//        Assert.assertEquals(3, rows.size)
//        Assert.assertTrue(rows.all { it.name.contains("bar", ignoreCase = true) })
//    }

    @Test
    fun emptyOnShortQuery() = runBlocking {
        val placesRepository = declareMock<PlacesRepository>()
        model.setQuery("b")
        val results = model.rows.blockingObserve()
        verifyNoInteractions(placesRepository)
        Assert.assertTrue(results.isEmpty())
    }

    @Test
    fun resetsLastSearch() = runBlocking {
        model.setQuery("bar")
        model.setQuery("")
        val results = model.rows.blockingObserve()
        Assert.assertTrue(results.isEmpty())
    }

    private fun generatePlace(name: String): Place {
        return Place(
            id = UUID.randomUUID().toString(),
            name = name,
            latitude = 0.0,
            longitude = 0.0,
            description = "",
            categoryId = UUID.randomUUID().toString(),
            phone = "",
            website = "",
            visible = true,
            openingHours = "",
            createdAt = DateTime.now().toString(),
            updatedAt = DateTime.now().toString()
        )
    }
}