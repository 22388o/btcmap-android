package com.bubelov.coins.repository

import com.bubelov.coins.TestSuite
import com.bubelov.coins.model.NotificationArea
import com.bubelov.coins.repository.area.NotificationAreaRepository
import com.google.gson.Gson
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import org.koin.core.inject
import org.koin.test.mock.declareMock
import org.mockito.ArgumentMatchers.anyString
import org.mockito.BDDMockito.*

class NotificationAreaRepositoryTests : TestSuite() {

    val repository: NotificationAreaRepository by inject()

    @Test
    fun returnsNullIfNotSet() = runBlocking {
        val preferencesRepository = declareMock<PreferencesRepository> {
            given(get(anyString())).willReturn(flowOf(""))
        }

        Assert.assertTrue(repository.getNotificationArea().first() == null)
        verify(preferencesRepository).get(anyString())
        Unit
    }

    @Test
    fun setNotificationArea() = runBlocking {
        val preferencesRepository = declareMock<PreferencesRepository>()

        val area = NotificationArea(
            latitude = 50.0,
            longitude = 0.0,
            radius = 100.0
        )

        repository.setNotificationArea(area)

        verify(preferencesRepository).put(
            key = PreferencesRepository.NOTIFICATION_AREA_KEY,
            value = Gson().toJson(area)
        )
    }
}