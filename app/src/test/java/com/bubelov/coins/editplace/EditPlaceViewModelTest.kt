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

package com.bubelov.coins.editplace

import android.arch.core.executor.testing.InstantTaskExecutorRule
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
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

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
            id = 0,
            name = "Crypto Library"
        )

        whenever(placesRepository.addPlace(place)).thenReturn(place)

        model.setUp(place)
        model.submitChanges()

        assertTrue(model.changesSubmitted.blockingObserve())
        verify(placesRepository).addPlace(place)
        verify(placesRepository, never()).updatePlace(place)
    }

    @Test
    fun updateExistingPlace() = runBlocking<Unit> {
        val place = emptyPlace().copy(
            id = 50,
            name = "Crypto Library"
        )

        whenever(placesRepository.updatePlace(place)).thenReturn(place)

        model.setUp(place)
        model.submitChanges()

        assertTrue(model.changesSubmitted.blockingObserve())
        verify(placesRepository).updatePlace(place)
        verify(placesRepository, never()).addPlace(place)
    }

    @Test
    fun handleFailure() = runBlocking {
        whenever(placesRepository.addPlace(any())).thenThrow(IllegalStateException("Test"))

        model.setUp(emptyPlace())
        model.submitChanges()
        Assert.assertNotNull(model.errorMessage.blockingObserve())
    }
}