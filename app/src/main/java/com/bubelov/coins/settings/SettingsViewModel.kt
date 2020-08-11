package com.bubelov.coins.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bubelov.coins.notifications.PlaceNotificationManager
import com.bubelov.coins.repository.PreferencesRepository
import com.bubelov.coins.repository.place.PlacesRepository
import com.bubelov.coins.repository.synclogs.LogsRepository
import com.bubelov.coins.sync.DatabaseSync
import com.bubelov.coins.util.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import java.time.LocalDateTime

class SettingsViewModel(
    private val placesRepository: PlacesRepository,
    private val databaseSync: DatabaseSync,
    private val logsRepository: LogsRepository,
    private val placeNotificationsManager: PlaceNotificationManager,
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {

    private val _syncLogs = LiveEvent<List<String>>()
    val syncLogs = _syncLogs.toSingleEvent()

    fun getDistanceUnits() = preferencesRepository.get(
        key = PreferencesRepository.DISTANCE_UNITS_KEY
    )

    suspend fun setDistanceUnits(value: String) {
        preferencesRepository.put(
            key = PreferencesRepository.DISTANCE_UNITS_KEY,
            value = value
        )
    }

    suspend fun syncDatabase() = databaseSync.sync()

    fun showSyncLogs() = viewModelScope.launch {
        val logs = logsRepository.getAll().first()
            .reversed()
            .map { "Date: ${LocalDateTime.parse(it.datetime)}, Message: ${it.message}" }

        if (isActive && logs.isNotEmpty()) {
            _syncLogs.value = logs
        }
    }

    suspend fun testNotification() {
        val randomPlace = placesRepository.findRandom()

        if (randomPlace != null) {
            placeNotificationsManager.issueNotification(randomPlace)
        }
    }
}