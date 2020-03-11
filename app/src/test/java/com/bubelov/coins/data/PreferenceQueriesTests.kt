package com.bubelov.coins.data

import com.bubelov.coins.Database
import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver
import org.junit.Before
import org.junit.Test
import java.util.*
import kotlin.random.Random

class PreferenceQueriesTests {

    lateinit var queries: PreferenceQueries

    @Before
    fun setUp() {
        val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        Database.Schema.create(driver)
        val database = Database(driver)
        queries = database.preferenceQueries
    }

    @Test
    fun emptyByDefault() {
        assert(queries.selectCount().executeAsOne() == 0L)
    }

    @Test
    fun insertOrReplace_insertsItem() {
        val item = testItem()
        queries.insertOrReplace(item)

        assert(queries.selectCount().executeAsOne() == 1L)
        assert(queries.selectByKey(item.key).executeAsOne() == item)
    }

    @Test
    fun insertOrReplace_replacesItem() {
        val item = testItem()
        queries.insertOrReplace(item)

        val updatedItem = item.copy(value = "Changed")
        queries.insertOrReplace(updatedItem)

        assert(queries.selectCount().executeAsOne() == 1L)
        assert(queries.selectByKey(item.key).executeAsOne() == updatedItem)
    }

    @Test
    fun selectAll_selectsAllItems() {
        val items = listOf(testItem(), testItem())

        queries.transaction {
            items.forEach {
                queries.insertOrReplace(it)
            }
        }

        assert(queries.selectAll().executeAsList() == items)
    }

    @Test
    @ExperimentalStdlibApi
    fun selectById_selectsCorrectItem() {
        val items = buildList<Preference> {
            repeat(100) {
                add(testItem())
            }
        }

        queries.transaction {
            items.forEach {
                queries.insertOrReplace(it)
            }
        }

        val randomItem = items.random()

        assert(queries.selectByKey(randomItem.key).executeAsOne() == randomItem)
    }

    @Test
    @ExperimentalStdlibApi
    fun selectCount_returnsCorrectCount() {
        val count = 1 + Random(System.currentTimeMillis()).nextInt(100)

        queries.transaction {
            repeat(count) {
                queries.insertOrReplace(testItem())
            }
        }

        assert(queries.selectCount().executeAsOne() == count.toLong())
    }

    private fun testItem() = Preference.Impl(
        key = "test_key_${UUID.randomUUID()}",
        value = "test_value_${UUID.randomUUID()}"
    )
}