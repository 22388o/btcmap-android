package settings

import androidx.lifecycle.ViewModel
import etc.PlaceNotificationManager
import map.PlacesRepository
import sync.DatabaseSync

class SettingsViewModel(
    private val placesRepository: PlacesRepository,
    private val databaseSync: DatabaseSync,
    private val placeNotificationsManager: PlaceNotificationManager,
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {

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

    suspend fun testNotification() {
        val randomPlace = placesRepository.findRandom()

        if (randomPlace != null) {
            placeNotificationsManager.issueNotification(randomPlace)
        }
    }
}