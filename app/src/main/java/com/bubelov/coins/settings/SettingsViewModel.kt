package com.bubelov.coins.settings

import androidx.lifecycle.ViewModel
import com.bubelov.coins.repository.place.PlacesRepository
import com.bubelov.coins.repository.synclogs.SyncLogsRepository
import com.bubelov.coins.sync.DatabaseSync
import com.bubelov.coins.util.*
import kotlinx.coroutines.*
import java.util.*
import kotlin.coroutines.CoroutineContext

class SettingsViewModel(
    private val placesRepository: PlacesRepository,
    distanceUnitsLiveData: DistanceUnitsLiveData,
    private val databaseSync: DatabaseSync,
    private val syncLogsRepository: SyncLogsRepository,
    private val placeNotificationsManager: PlaceNotificationManager,
    coroutineContext: CoroutineContext
) : ViewModel() {
    private val job = Job()
    private val uiScope = CoroutineScope(coroutineContext + job)

    val distanceUnits = distanceUnitsLiveData

    private val _syncLogs = LiveEvent<List<String>>()
    val syncLogs = _syncLogs.toSingleEvent()

    fun syncDatabase() = uiScope.launch {
        databaseSync.sync()
    }

    fun showSyncLogs() = uiScope.launch {
        val logs = syncLogsRepository.all()
            .reversed()
            .map { "Date: ${Date(it.time)}, Affected places: ${it.affectedPlaces}" }

        if (isActive && logs.isNotEmpty()) {
            _syncLogs.value = logs
        }
    }

    fun testNotification() = uiScope.launch {
        val randomPlace = placesRepository.findRandom()

        if (randomPlace != null) {
            placeNotificationsManager.issueNotification(randomPlace)
        }
    }
}