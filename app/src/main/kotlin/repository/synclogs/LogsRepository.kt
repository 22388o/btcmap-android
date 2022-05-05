package repository.synclogs

import com.squareup.sqldelight.runtime.coroutines.asFlow
import db.LogEntry
import db.LogEntryQueries
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.time.LocalDateTime

class LogsRepository(
    private val queries: LogEntryQueries
) {
    fun getAll() = queries.selectAll().asFlow().map { it.executeAsList() }

    fun append(tag: String = "", message: String) = runBlocking {
        withContext(Dispatchers.IO) {
            queries.insert(
                LogEntry(
                    datetime = LocalDateTime.now().toString(),
                    tag = tag,
                    message = message
                )
            )
        }
    }

    operator fun plusAssign(entry: LogEntry) = runBlocking {

        queries.insert(entry)
    }

    operator fun plusAssign(message: String) {
        append("", message)
    }
}

fun Any.logEntry(message: String) = LogEntry(
    datetime = LocalDateTime.now().toString(),
    tag = this.javaClass.simpleName,
    message = message
)