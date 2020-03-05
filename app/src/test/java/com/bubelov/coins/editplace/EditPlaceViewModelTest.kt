package com.bubelov.coins.editplace

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.bubelov.coins.emptyPlace
import com.bubelov.coins.repository.place.PlacesRepository
import com.bubelov.coins.util.blockingObserve
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import java.util.*

class EditPlaceViewModelTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock private lateinit var placesRepository: PlacesRepository
    private lateinit var model: EditPlaceViewModel

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)

        model = EditPlaceViewModel(
            placesRepository = placesRepository,
            coroutineContext = Dispatchers.Default
        )
    }

    @Test
    fun submitNewPlace() = runBlocking<Unit> {
        val place = emptyPlace().copy(
            id = UUID.randomUUID().toString(),
            name = "Crypto Library"
        )

        whenever(placesRepository.addPlace(place)).thenReturn(place)

        model.submitChanges(null, place)
        model.changesSubmitted.blockingObserve()

        verify(placesRepository).addPlace(place)
        verify(placesRepository, never()).updatePlace(place)
    }

    @Test
    fun updateExistingPlace() = runBlocking<Unit> {
        val originalPlace = emptyPlace().copy(
            id = UUID.randomUUID().toString(),
            name = "Crypto Library"
        )

        val updatedPlace = originalPlace.copy(name = "Crypto Exchange")

        whenever(placesRepository.updatePlace(updatedPlace)).thenReturn(updatedPlace)

        model.submitChanges(originalPlace, updatedPlace)
        model.changesSubmitted.blockingObserve()

        verify(placesRepository).updatePlace(updatedPlace)
        verify(placesRepository, never()).addPlace(any())
    }

    @Test
    fun handleFailure() = runBlocking {
        whenever(placesRepository.addPlace(any())).thenThrow(IllegalStateException("Test"))
        model.submitChanges(null, emptyPlace())
        Assert.assertNotNull(model.error.blockingObserve())
    }
}