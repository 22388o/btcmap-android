package com.bubelov.coins.search

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import android.content.res.Resources
import com.bubelov.coins.data.Place
import com.bubelov.coins.repository.PreferencesRepository
import com.bubelov.coins.repository.place.PlacesRepository
import com.bubelov.coins.repository.placecategory.PlaceCategoriesRepository
import com.bubelov.coins.repository.placeicon.PlaceIconsRepository
import com.bubelov.coins.util.blockingObserve
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyZeroInteractions
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.setMain
import org.joda.time.DateTime
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import java.util.*
import kotlin.time.ExperimentalTime

@ExperimentalCoroutinesApi
@ExperimentalTime
class PlacesSearchViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock private lateinit var placesRepository: PlacesRepository
    @Mock private lateinit var placeCategoriesRepository: PlaceCategoriesRepository
    @Mock private lateinit var placeIconsRepository: PlaceIconsRepository
    @Mock private lateinit var preferencesRepository: PreferencesRepository
    @Mock private lateinit var resources: Resources

    private lateinit var model: PlacesSearchViewModel

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)

        model = PlacesSearchViewModel(
            placesRepository,
            placeCategoriesRepository,
            placeIconsRepository,
            preferencesRepository,
            resources
        )

        model.setUp(null)

        runBlocking {
            whenever(placesRepository.findBySearchQuery(anyString()))
                .thenReturn(
                    listOf(
                        generatePlace("Bar 1"),
                        generatePlace("Bar 2"),
                        generatePlace("Bar 3")
                    )
                )

            whenever(placeIconsRepository.getPlaceIcon(anyString()))
                .thenReturn(mock())
        }
    }

    @Test
    fun searchBars() = runBlocking {
        Dispatchers.setMain(Dispatchers.Default)
        model.setQuery("bar")
        val rows = model.rows.blockingObserve()
        verify(placesRepository).findBySearchQuery("bar")
        Assert.assertEquals(3, rows.size)
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

    private fun generatePlace(name: String): Place {
        return Place.Impl(
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