package com.bubelov.coins.notificationarea

import com.bubelov.coins.model.Location
import com.bubelov.coins.model.NotificationArea
import com.bubelov.coins.repository.area.NotificationAreaRepository
import com.bubelov.coins.repository.placeicon.PlaceIconsRepository
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class NotificationAreaViewModelTest {

    @Mock private lateinit var notificationAreaRepository: NotificationAreaRepository
    @Mock private lateinit var placeIconsRepository: PlaceIconsRepository

    private lateinit var defaultLocation: Location

    private lateinit var model: NotificationAreaViewModel

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        defaultLocation = Location(50.0, 1.0)

        model = NotificationAreaViewModel(
            notificationAreaRepository,
            placeIconsRepository,
            defaultLocation
        )
    }

    @Test
    fun getNotificationArea_withEmptyCache_returnsDefaultArea() = runBlocking {
        val defaultArea = NotificationArea(
            latitude = defaultLocation.latitude,
            longitude = defaultLocation.longitude,
            radius = NotificationAreaRepository.DEFAULT_RADIUS_METERS
        )

        whenever(notificationAreaRepository.getNotificationArea()).thenReturn(flowOf(null))

        val area = model.getNotificationArea()

        verify(notificationAreaRepository).getNotificationArea()
        verifyNoMoreInteractions(notificationAreaRepository)

        assert(area == defaultArea)
    }

    @Test
    fun getNotificationArea_withSavedArea_returnsSavedArea() = runBlocking {
        val area = NotificationArea(10.0, 20.0, 30.0)
        whenever(notificationAreaRepository.getNotificationArea()).thenReturn(flowOf(area))

        assert(
            area == model.getNotificationArea()
        )

        verify(notificationAreaRepository).getNotificationArea()
        verifyNoMoreInteractions(notificationAreaRepository)
    }
}