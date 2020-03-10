package com.bubelov.coins.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bubelov.coins.repository.place.PlacesRepository
import com.bubelov.coins.repository.synclogs.SyncLogsRepository
import com.bubelov.coins.sync.DatabaseSync
import com.bubelov.coins.util.*
import kotlinx.coroutines.*
import java.util.*

class SettingsViewModel(
    private val placesRepository: PlacesRepository,
    distanceUnitsLiveData: DistanceUnitsLiveData,
    private val databaseSync: DatabaseSync,
    private val syncLogsRepository: SyncLogsRepository,
    private val placeNotificationsManager: PlaceNotificationManager
) : ViewModel() {

    val distanceUnits = distanceUnitsLiveData

    private val _syncLogs = LiveEvent<List<String>>()
    val syncLogs = _syncLogs.toSingleEvent()

    suspend fun syncDatabase() = databaseSync.sync()

    fun showSyncLogs() = viewModelScope.launch {
        val logs = syncLogsRepository.all()
            .reversed()
            .map { "Date: ${Date(it.time)}, Affected places: ${it.affectedPlaces}" }

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