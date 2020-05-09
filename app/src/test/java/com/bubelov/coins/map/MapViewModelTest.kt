package com.bubelov.coins.map

import com.bubelov.coins.emptyPlace
import com.bubelov.coins.repository.LocationRepository
import com.bubelov.coins.repository.place.PlacesRepository
import com.bubelov.coins.repository.placecategory.PlaceCategoriesRepository
import com.bubelov.coins.repository.placeicon.PlaceIconsRepository
import com.bubelov.coins.repository.user.UserRepository
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import kotlin.time.ExperimentalTime

@ExperimentalTime
class MapViewModelTest {

    @Mock private lateinit var placesRepository: PlacesRepository
    @Mock private lateinit var userRepository: UserRepository
    @Mock private lateinit var locationRepository: LocationRepository
    @Mock private lateinit var placeIconsRepository: PlaceIconsRepository
    @Mock private lateinit var placeCategoriesRepository: PlaceCategoriesRepository

    private lateinit var model: MapViewModel

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)

        model = MapViewModel(
            placesRepository,
            userRepository,
            locationRepository,
            placeIconsRepository,
            placeCategoriesRepository
        )
    }

    @Test
    fun getSelectedPlaceFlow_returnsNull() = runBlocking {
        val place = model.selectedPlaceFlow.take(1).first()
        assert(place == null)
    }

    @Test
    fun setSelectedPlace() = runBlocking {
        val place = emptyPlace().copy(name = "Test")
        whenever(placesRepository.find(place.id)).thenReturn(place)
        model.selectPlace(place.id)
        verify(placesRepository).getAll()
        verify(placesRepository).find(place.id)
        verifyNoMoreInteractions(placesRepository)
        assert(model.selectedPlaceFlow.take(1).single() == place)
    }

    @Test
    fun onAddPlaceClick_returnsUnauthorized_whenUnauthorized() = runBlocking {
        whenever(userRepository.getToken()).thenReturn("")
        val result = model.onAddPlaceClick()
        verify(userRepository).getToken()
        assert(result == MapViewModel.AddPlaceClickResult.UNAUTHORIZED)
    }

    @Test
    fun onEditPlaceClick_returnsUnauthorized_whenUnauthorized() = runBlocking<Unit> {
        whenever(userRepository.getToken()).thenReturn("")
        val result = model.onEditPlaceClick()
        verify(userRepository).getToken()
        assert(result == MapViewModel.EditPlaceClickResult.UNAUTHORIZED)
    }

    @Test
    fun onDrawerHeaderClick_returnsUnauthorized_whenUnauthorized() = runBlocking {
        whenever(userRepository.getToken()).thenReturn("")
        val result = model.onDrawerHeaderClick()
        verify(userRepository).getToken()
        assert(result == MapViewModel.DrawerHeaderClickResult.REQUIRE_AUTH)
    }

    @Test
    fun onAddPlaceClick_allowsAccess_whenAuthorized() = runBlocking {
        whenever(userRepository.getToken()).thenReturn("token")
        val result = model.onAddPlaceClick()
        verify(userRepository).getToken()
        assert(result == MapViewModel.AddPlaceClickResult.ALLOWED)
    }

    @Test
    fun onEditPlaceClick_allowsAccess_whenAuthorized() = runBlocking<Unit> {
        whenever(userRepository.getToken()).thenReturn("token")
        val result = model.onEditPlaceClick()
        verify(userRepository).getToken()
        assert(result == MapViewModel.EditPlaceClickResult.ALLOWED)
    }

    @Test
    fun onDrawerHeaderClick_allowsAccess_whenAuthorized() = runBlocking {
        whenever(userRepository.getToken()).thenReturn("token")
        val result = model.onDrawerHeaderClick()
        verify(userRepository).getToken()
        assert(result == MapViewModel.DrawerHeaderClickResult.SHOW_USER_PROFILE)
    }
}