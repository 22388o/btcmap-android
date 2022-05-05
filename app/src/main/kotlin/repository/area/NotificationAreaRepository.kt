package repository.area

import model.NotificationArea
import repository.PreferencesRepository
import repository.PreferencesRepository.Companion.NOTIFICATION_AREA_KEY
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class NotificationAreaRepository(
    private val preferencesRepository: PreferencesRepository,
    private val gson: Gson
) {
    fun getNotificationArea(): Flow<NotificationArea?> {
        return preferencesRepository.get(NOTIFICATION_AREA_KEY).map {
            gson.fromJson(it, NotificationArea::class.java)
        }
    }

    suspend fun setNotificationArea(area: NotificationArea) {
        preferencesRepository.put(
            key = NOTIFICATION_AREA_KEY,
            value = gson.toJson(area)
        )
    }

    companion object {
        const val DEFAULT_RADIUS_METERS = 50000.0
    }
}