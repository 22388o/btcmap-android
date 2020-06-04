package com.bubelov.coins.map

import com.bubelov.coins.TestSuite
import com.bubelov.coins.emptyPlace
import com.bubelov.coins.repository.place.PlacesRepository
import com.bubelov.coins.repository.user.UserRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.koin.core.inject
import org.koin.test.mock.declareMock
import org.mockito.BDDMockito.*

class MapViewModelTests : TestSuite() {

    val model: MapViewModel by inject()

    @Test
    fun getSelectedPlaceFlow_returnsNull() = runBlocking {
        val place = model.selectedPlaceFlow.take(1).first()
        assert(place == null)
    }

    @Test
    fun setSelectedPlace() = runBlocking {
        val placesRepository = declareMock<PlacesRepository>()
        val place = emptyPlace().copy(name = "Test")
        given(placesRepository.find(place.id)).willReturn(place)
        model.selectPlace(place.id)
        verify(placesRepository).getAll()
        verify(placesRepository).find(place.id)
        verifyNoMoreInteractions(placesRepository)
        assert(model.selectedPlaceFlow.take(1).single() == place)
    }

    @Test
    fun onAddPlaceClick_returnsUnauthorized_whenUnauthorized() = runBlocking {
        val userRepository = declareMock<UserRepository>()
        given(userRepository.getToken()).willReturn("")
        val result = model.onAddPlaceClick()
        verify(userRepository).getToken()
        assert(result == MapViewModel.AddPlaceClickResult.UNAUTHORIZED)
    }

    @Test
    fun onEditPlaceClick_returnsUnauthorized_whenUnauthorized() = runBlocking<Unit> {
        val userRepository = declareMock<UserRepository>()
        given(userRepository.getToken()).willReturn("")
        val result = model.onEditPlaceClick()
        verify(userRepository).getToken()
        assert(result == MapViewModel.EditPlaceClickResult.UNAUTHORIZED)
    }

    @Test
    fun onDrawerHeaderClick_returnsUnauthorized_whenUnauthorized() = runBlocking {
        val userRepository = declareMock<UserRepository>()
        given(userRepository.getToken()).willReturn("")
        val result = model.onDrawerHeaderClick()
        verify(userRepository).getToken()
        assert(result == MapViewModel.DrawerHeaderClickResult.REQUIRE_AUTH)
    }

    @Test
    fun onAddPlaceClick_allowsAccess_whenAuthorized() = runBlocking {
        val userRepository = declareMock<UserRepository>()
        given(userRepository.getToken()).willReturn("token")
        val result = model.onAddPlaceClick()
        verify(userRepository).getToken()
        assert(result == MapViewModel.AddPlaceClickResult.ALLOWED)
    }

    @Test
    fun onEditPlaceClick_allowsAccess_whenAuthorized() = runBlocking<Unit> {
        val userRepository = declareMock<UserRepository>()
        given(userRepository.getToken()).willReturn("token")
        val result = model.onEditPlaceClick()
        verify(userRepository).getToken()
        assert(result == MapViewModel.EditPlaceClickResult.ALLOWED)
    }

    @Test
    fun onDrawerHeaderClick_allowsAccess_whenAuthorized() = runBlocking {
        val userRepository = declareMock<UserRepository>()
        given(userRepository.getToken()).willReturn("token")
        val result = model.onDrawerHeaderClick()
        verify(userRepository).getToken()
        assert(result == MapViewModel.DrawerHeaderClickResult.SHOW_USER_PROFILE)
    }
}