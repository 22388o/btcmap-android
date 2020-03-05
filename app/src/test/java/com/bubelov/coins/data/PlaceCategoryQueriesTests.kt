package com.bubelov.coins.data

import com.bubelov.coins.Database
import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver
import org.joda.time.DateTime
import org.junit.Before
import org.junit.Test
import java.util.*
import kotlin.random.Random

class PlaceCategoryQueriesTests {

    lateinit var queries: PlaceCategoryQueries

    @Before
    fun setUp() {
        val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        Database.Schema.create(driver)
        val database = Database(driver)
        queries = database.placeCategoryQueries
    }

    @Test
    fun emptyByDefault() {
        assert(queries.selectCount().executeAsOne() == 0L)
    }

    @Test
    fun insertOrReplace_insertsItem() {
        val item = placeCategory()
        queries.insertOrReplace(item)

        assert(queries.selectCount().executeAsOne() == 1L)
        assert(queries.selectById(item.id).executeAsOne() == item)
    }

    @Test
    fun insertOrReplace_replacesItem() {
        val item = placeCategory()
        queries.insertOrReplace(item)

        val updatedItem = item.copy(name = "Changed")
        queries.insertOrReplace(updatedItem)

        assert(queries.selectCount().executeAsOne() == 1L)
        assert(queries.selectById(item.id).executeAsOne() == updatedItem)
    }

    @Test
    fun selectAll_selectsAllItems() {
        val items = listOf(placeCategory(), placeCategory())

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
        val items = buildList<PlaceCategory> {
            repeat(100) {
                add(placeCategory())
            }
        }

        queries.transaction {
            items.forEach {
                queries.insertOrReplace(it)
            }
        }

        val randomItem = items.random()

        assert(queries.selectById(randomItem.id).executeAsOne() == randomItem)
    }

    @Test
    @ExperimentalStdlibApi
    fun selectCount_returnsCorrectCount() {
        val count = 1 + Random(System.currentTimeMillis()).nextInt(100)

        queries.transaction {
            repeat(count) {
                queries.insertOrReplace(placeCategory())
            }
        }

        assert(queries.selectCount().executeAsOne() == count.toLong())
    }

    @Test
    fun selectMaxUpdatedAt_selectsCorrectItem() {
        val count = 1 + Random(System.currentTimeMillis()).nextInt(100)

        queries.transaction {
            repeat(count) {
                queries.insertOrReplace(placeCategory())
            }
        }

        val item = placeCategory()
        val updatedAt = DateTime.parse(item.updatedAt).plusYears(5).toString()
        queries.insertOrReplace(item.copy(updatedAt = updatedAt))

        assert(queries.selectMaxUpdatedAt().executeAsOne().MAX == updatedAt)
    }

    private fun placeCategory() = PlaceCategory.Impl(
        id = UUID.randomUUID().toString(),
        name = "Test",
        createdAt = DateTime.now().toString(),
        updatedAt = DateTime.now().toString()
    )
}