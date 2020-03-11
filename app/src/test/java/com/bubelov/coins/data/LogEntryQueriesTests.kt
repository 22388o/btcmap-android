package com.bubelov.coins.data

import com.bubelov.coins.Database
import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver
import org.joda.time.DateTime
import org.junit.Before
import org.junit.Test
import java.util.*
import kotlin.random.Random

class LogEntryQueriesTests {

    lateinit var queries: LogEntryQueries

    @Before
    fun setUp() {
        val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        Database.Schema.create(driver)
        val database = Database(driver)
        queries = database.logEntryQueries
    }

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

        assert(queries.selectAll().executeAsList() == items)
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