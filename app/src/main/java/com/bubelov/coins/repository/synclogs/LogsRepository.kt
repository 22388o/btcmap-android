package com.bubelov.coins.repository.synclogs

import com.bubelov.coins.data.LogEntry
import com.bubelov.coins.data.LogEntryQueries
import com.squareup.sqldelight.runtime.coroutines.asFlow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.joda.time.DateTime

class LogsRepository(
    private val queries: LogEntryQueries
) {
    fun getAll() = queries.selectAll().asFlow().map { it.executeAsList() }

    fun appendBlocking(tag: String, message: String) = runBlocking {
        append(tag, message)
    }

    suspend fun append(tag: String, message: String) {
        withContext(Dispatchers.IO) {
            queries.insert(
                LogEntry(
                    datetime = DateTime.now().toString(),
                    tag = tag,
                    message = message
                )
            )
        }
    }
}