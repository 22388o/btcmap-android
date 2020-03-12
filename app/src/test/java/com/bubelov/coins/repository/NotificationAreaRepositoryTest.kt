package com.bubelov.coins.repository

import com.bubelov.coins.model.NotificationArea
import com.bubelov.coins.repository.area.NotificationAreaRepository
import com.google.gson.Gson
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.MockitoAnnotations

@ExperimentalCoroutinesApi
class NotificationAreaRepositoryTest {

    @Mock private lateinit var preferencesRepository: PreferencesRepository

    private lateinit var repository: NotificationAreaRepository

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        repository = NotificationAreaRepository(preferencesRepository, Gson())
    }

    @Test
    fun returnsNullIfNotSet() = runBlocking {
        whenever(preferencesRepository.get(anyString())).thenReturn(flowOf(""))
        Assert.assertTrue(repository.getNotificationArea().first() == null)
        verify(preferencesRepository).get(anyString())
        Unit
    }

    @Test
    fun setNotificationArea() = runBlocking {
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