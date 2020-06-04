package com.bubelov.coins.editplace

import com.bubelov.coins.TestSuite
import com.bubelov.coins.emptyPlace
import com.bubelov.coins.repository.place.PlacesRepository
import com.bubelov.coins.util.BasicTaskState
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import org.koin.core.inject
import org.koin.test.mock.declareMock
import java.util.*
import org.mockito.BDDMockito.*

class EditPlaceViewModelTests : TestSuite() {

    val model: EditPlaceViewModel by inject()

    @Test
    fun submitChanges_withNewPlace_addsPlace() = runBlocking<Unit> {
        val place = emptyPlace().copy(
            id = UUID.randomUUID().toString(),
            name = "Crypto Library"
        )

        val placesRepository = declareMock<PlacesRepository> {
            given(addPlace(place)).willReturn(place)
        }

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

        val placesRepository = declareMock<PlacesRepository> {
            given(updatePlace(updatedPlace)).willReturn(updatedPlace)
        }

        model.submitChanges(originalPlace, updatedPlace).collect()

        verify(placesRepository).updatePlace(updatedPlace)
        verifyNoMoreInteractions(placesRepository)
    }

    @Test
    fun submitChanges_handlesFailure() = runBlocking {
        val place = emptyPlace()
        val exception = Exception("Test exception")

        declareMock<PlacesRepository> {
            given(addPlace(place)).will {
                throw exception
            }
        }

        val lastState = model.submitChanges(null, place).toList().last()
        assertEquals(BasicTaskState.Error::class.java, lastState::class.java)
    }
}