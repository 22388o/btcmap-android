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

package com.bubelov.coins.map

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.bubelov.coins.repository.area.NotificationAreaRepository
import com.bubelov.coins.repository.place.PlacesRepository
import com.bubelov.coins.repository.placecategories.PlaceCategoriesRepository
import com.bubelov.coins.repository.placeicon.PlaceIconsRepository
import com.bubelov.coins.repository.user.UserRepository
import com.bubelov.coins.util.LocationLiveData
import com.bubelov.coins.util.blockingObserve
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class MapViewModelTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock private lateinit var notificationAreaRepository: NotificationAreaRepository
    @Mock private lateinit var placesRepository: PlacesRepository
    @Mock private lateinit var placeCategoriesRepository: PlaceCategoriesRepository
    @Mock private lateinit var placeIconsRepository: PlaceIconsRepository
    @Mock private lateinit var location: LocationLiveData
    @Mock private lateinit var userRepository: UserRepository
    private lateinit var model: MapViewModel

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)

        model = MapViewModel(
            notificationAreaRepository,
            placesRepository,
            placeCategoriesRepository,
            placeIconsRepository,
            location,
            userRepository,
            Dispatchers.Default
        )
    }

    @Test
    fun redirectsToAuthOnAddPlaceIfUnauthorized() = runBlocking<Unit> {
        whenever(userRepository.signedIn()).thenReturn(false)
        model.onAddPlaceClick()
        model.openSignInScreen.blockingObserve()
        verify(userRepository).signedIn()
    }

    @Test
    fun redirectsToAuthOnEditPlaceIfUnauthorized() = runBlocking<Unit> {
        whenever(userRepository.signedIn()).thenReturn(false)
        model.onEditPlaceClick()
        model.openSignInScreen.blockingObserve()
        verify(userRepository).signedIn()
    }

    @Test
    fun opensAddPlaceScreen() = runBlocking<Unit> {
        whenever(userRepository.signedIn()).thenReturn(true)
        model.onAddPlaceClick()
        model.openAddPlaceScreen.blockingObserve()
        verify(userRepository).signedIn()
    }

    @Test
    fun opensEditPlaceScreen() = runBlocking<Unit> {
        whenever(userRepository.signedIn()).thenReturn(true)
        model.onEditPlaceClick()
        model.openEditPlaceScreen.blockingObserve()
        verify(userRepository).signedIn()
    }
}