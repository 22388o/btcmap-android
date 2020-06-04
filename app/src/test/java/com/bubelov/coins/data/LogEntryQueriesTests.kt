package com.bubelov.coins.data

import com.bubelov.coins.TestSuite
import org.joda.time.DateTime
import org.junit.Test
import org.koin.core.inject
import java.util.*
import kotlin.random.Random

class LogEntryQueriesTests : TestSuite() {

    private val queries: LogEntryQueries by inject()

    @Test
    fun emptyByDefault() {
        assert(queries.selectCount().executeAsOne() == 0L)
    }

    @Test
    fun insert_insertsItem() {
        val item = testItem()
        queries.insert(item)

        assert(queries.selectCount().executeAsOne() == 1L)
        assert(queries.selectByTag(item.tag).executeAsOne() == item)
    }

    @Test
    fun selectAll_selectsAllItems() {
        val items = listOf(testItem(), testItem())

        queries.transaction {
            items.forEach {
                queries.insert(it)
            }
        }

        assert(queries.selectAll().executeAsList().size == items.size)
    }

    @Test
    @ExperimentalStdlibApi
    fun selectByTag_selectsCorrectItem() {
        val items = buildList<LogEntry> {
            repeat(100) {
                add(testItem())
            }
        }

        queries.transaction {
            items.forEach {
                queries.insert(it)
            }
        }

        val randomItem = items.random()

        assert(queries.selectByTag(randomItem.tag).executeAsOne() == randomItem)
    }

    @Test
    @ExperimentalStdlibApi
    fun selectCount_returnsCorrectCount() {
        val count = 1 + Random(System.currentTimeMillis()).nextInt(100)

        queries.transaction {
            repeat(count) {
                queries.insert(testItem())
            }
        }

        assert(queries.selectCount().executeAsOne() == count.toLong())
    }

    private fun testItem() = LogEntry.Impl(
        datetime = DateTime.now().toString(),
        tag = "test_tag_${UUID.randomUUID()}",
        message = "test_massage_${UUID.randomUUID()}"
    )
}