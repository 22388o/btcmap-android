package com.bubelov.coins.editplace

import com.bubelov.coins.emptyPlace
import com.bubelov.coins.repository.place.PlacesRepository
import com.bubelov.coins.util.BasicTaskState
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import java.util.*

class EditPlaceViewModelTest {

    @Mock private lateinit var placesRepository: PlacesRepository

    private lateinit var model: EditPlaceViewModel

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        model = EditPlaceViewModel(placesRepository)
    }

    @Test
    fun submitChanges_withNewPlace_addsPlace() = runBlocking<Unit> {
        val place = emptyPlace().copy(
            id = UUID.randomUUID().toString(),
            name = "Crypto Library"
        )

        whenever(placesRepository.addPlace(place)).thenReturn(place)

        model.submitChanges(null, place).collect()

        verify(placesRepository).addPlace(place)
        verify(placesRepository, never()).updatePlace(place)
    }

    @Test
    fun submitChanges_withExistingPlace_updatesPlace() = runBlocking<Unit> {
        val originalPlace = emptyPlace().copy(
            id = UUID.randomUUID().toString(),
            name = "Crypto Library"
        )

        val updatedPlace = originalPlace.copy(name = "Crypto Exchange")

        whenever(placesRepository.updatePlace(updatedPlace)).thenReturn(updatedPlace)

        model.submitChanges(originalPlace, updatedPlace).collect()

        verify(placesRepository).updatePlace(updatedPlace)
        verify(placesRepository, never()).addPlace(any())
    }

    @Test
    fun submitChanges_handlesFailure() = runBlocking {
        val exception = Exception("Test exception")

        whenever(placesRepository.addPlace(any())).then {
            throw exception
        }

        val lastState = model.submitChanges(null, emptyPlace()).toList().last()
        assert(lastState is BasicTaskState.Error && lastState.message == exception.message)
    }
}