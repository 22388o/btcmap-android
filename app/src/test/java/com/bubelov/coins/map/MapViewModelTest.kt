package com.bubelov.coins.map

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.bubelov.coins.repository.area.NotificationAreaRepository
import com.bubelov.coins.repository.currency.CurrenciesRepository
import com.bubelov.coins.repository.currencyplace.CurrenciesPlacesRepository
import com.bubelov.coins.repository.place.PlacesRepository
import com.bubelov.coins.repository.placecategory.PlaceCategoriesRepository
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
    @Mock private lateinit var currenciesRepository: CurrenciesRepository
    @Mock private lateinit var currenciesPlacesRepository: CurrenciesPlacesRepository

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
            currenciesRepository,
            currenciesPlacesRepository,
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