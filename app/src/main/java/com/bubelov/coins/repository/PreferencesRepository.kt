package com.bubelov.coins.repository

import com.bubelov.coins.data.Preference
import com.bubelov.coins.data.PreferenceQueries
import com.squareup.sqldelight.runtime.coroutines.asFlow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

@ExperimentalCoroutinesApi
class PreferencesRepository(
    private val queries: PreferenceQueries
) {
    suspend fun put(key: String, value: String) {
        withContext(Dispatchers.IO) {
            queries.insertOrReplace(
                Preference.Impl(key, value)
            )
        }
    }

    fun get(key: String): Flow<String> {
        return queries.selectByKey(key).asFlow()
            .map { it.executeAsOneOrNull()?.value ?: "" }
    }

    fun getAll() = queries.selectAll().asFlow().map { it.executeAsList() }

    fun getCount() = queries.selectCount().asFlow().map { it.executeAsOne() }

    companion object {
        const val PERMISSIONS_EXPLAINED_KEY = "permissions_explained"
        const val NOTIFICATION_AREA_KEY = "notification_area"
        const val DISTANCE_UNITS_KEY = "distance_units"
    }
}