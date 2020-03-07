package com.bubelov.coins.repository.area

import android.content.SharedPreferences
import com.bubelov.coins.model.NotificationArea
import com.google.gson.Gson

class NotificationAreaRepository(
    private val preferences: SharedPreferences,
    private val gson: Gson
) {
    var notificationArea: NotificationArea?
        get() {
            return gson.fromJson(
                preferences.getString(NOTIFICATION_AREA_KEY, ""),
                NotificationArea::class.java
            )
        }
        set(area) {
            preferences.edit().putString(
                NOTIFICATION_AREA_KEY,
                gson.toJson(area)
            ).apply()
        }

    companion object {
        private const val NOTIFICATION_AREA_KEY = "notificationArea"
        const val DEFAULT_RADIUS_METERS = 50000.0
    }
}