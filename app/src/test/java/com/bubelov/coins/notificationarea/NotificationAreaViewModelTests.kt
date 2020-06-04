package com.bubelov.coins.notificationarea

import com.bubelov.coins.TestSuite
import com.bubelov.coins.model.Location
import com.bubelov.coins.model.NotificationArea
import com.bubelov.coins.repository.area.NotificationAreaRepository
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.koin.core.get
import org.koin.core.inject
import org.koin.test.mock.declareMock
import org.mockito.BDDMockito.*

class NotificationAreaViewModelTests : TestSuite() {

    val model: NotificationAreaViewModel by inject()

    @Test
    fun getNotificationArea_withEmptyCache_returnsDefaultArea() = runBlocking {
        val defaultLocation = get<Location>()

        val defaultArea = NotificationArea(
            latitude = defaultLocation.latitude,
            longitude = defaultLocation.longitude,
            radius = NotificationAreaRepository.DEFAULT_RADIUS_METERS
        )

        val notificationAreaRepository = declareMock<NotificationAreaRepository> {
            given(getNotificationArea()).willReturn(flowOf(null))
        }

        val area = model.getNotificationArea()

        verify(notificationAreaRepository).getNotificationArea()
        verifyNoMoreInteractions(notificationAreaRepository)

        assert(area == defaultArea)
    }

    @Test
    fun getNotificationArea_withSavedArea_returnsSavedArea() = runBlocking {
        val area = NotificationArea(10.0, 20.0, 30.0)

        val notificationAreaRepository = declareMock<NotificationAreaRepository> {
            given(getNotificationArea()).willReturn(flowOf(area))
        }

        assert(
            area == model.getNotificationArea()
        )

        verify(notificationAreaRepository).getNotificationArea()
        verifyNoMoreInteractions(notificationAreaRepository)
    }
}