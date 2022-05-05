package com.bubelov.coins.repository

import com.bubelov.coins.data.Preference
import com.bubelov.coins.data.PreferenceQueries
import com.squareup.sqldelight.runtime.coroutines.asFlow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class PreferencesRepository(
    private val queries: PreferenceQueries
) {
    suspend fun put(key: String, value: String) {
        withContext(Dispatchers.IO) {
            queries.insertOrReplace(
                Preference(key, value)
            )
        }
    }

    fun get(key: String): Flow<String> {
        return queries.selectByKey(key).asFlow()
            .map { it.executeAsOneOrNull()?.value_ ?: "" }
    }

    fun getAll() = queries.selectAll().asFlow().map { it.executeAsList() }

    fun getCount() = queries.selectCount().asFlow().map { it.executeAsOne() }

    companion object {
        const val NOTIFICATION_AREA_KEY = "notification_area"
        const val DISTANCE_UNITS_KEY = "distance_units"
    }
}