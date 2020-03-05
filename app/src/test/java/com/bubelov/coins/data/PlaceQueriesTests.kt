package com.bubelov.coins.data

import com.bubelov.coins.Database
import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver
import org.joda.time.DateTime
import org.junit.Before
import org.junit.Test
import java.util.*
import kotlin.random.Random

class PlaceQueriesTests {

    lateinit var queries: PlaceQueries

    @Before
    fun setUp() {
        val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        Database.Schema.create(driver)
        val database = Database(driver)
        queries = database.placeQueries
    }

    @Test
    fun emptyByDefault() {
        assert(queries.selectCount().executeAsOne() == 0L)
    }

    @Test
    fun insertOrReplace_insertsItem() {
        val item = place()
        queries.insertOrReplace(item)

        assert(queries.selectCount().executeAsOne() == 1L)
        assert(queries.selectById(item.id).executeAsOne() == item)
    }

    @Test
    fun insertOrReplace_replacesItem() {
        val item = place()
        queries.insertOrReplace(item)

        val updatedItem = item.copy(name = "Changed")
        queries.insertOrReplace(updatedItem)

        assert(queries.selectCount().executeAsOne() == 1L)
        assert(queries.selectById(item.id).executeAsOne() == updatedItem)
    }

    @Test
    fun selectAll_selectsAllItems() {
        val items = listOf(place(), place())

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
        val items = buildList<Place> {
            repeat(100) {
                add(place())
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
    fun selectBySearchQuery_searchesByName() {
        queries.transaction {
            repeat(100) {
                var place = place()

                if (it == 1) {
                    place = place.copy(name = "Good Cafe")
                }

                if (it == 2) {
                    place = place.copy(name = "Bad Cafe")
                }

                queries.insertOrReplace(place)
            }
        }

        assert(queries.selectBySearchQuery("cafe").executeAsList().size == 2)
    }

    @Test
    fun selectRandom_returnsRandomItem() {
        val count = 1 + Random(System.currentTimeMillis()).nextInt(100)

        queries.transaction {
            repeat(count) {
                queries.insertOrReplace(place())
            }
        }

        var lastResult = queries.selectRandom().executeAsOneOrNull()

        while (queries.selectRandom().executeAsOne() == lastResult) {
            lastResult = queries.selectRandom().executeAsOneOrNull()
        }

        assert(true)
    }

    @Test
    fun selectCount_returnsCorrectCount() {
        val count = 1 + Random(System.currentTimeMillis()).nextInt(100)

        queries.transaction {
            repeat(count) {
                queries.insertOrReplace(place())
            }
        }

        assert(queries.selectCount().executeAsOne() == count.toLong())
    }

    @Test
    fun selectMaxUpdatedAt_selectsCorrectItem() {
        val count = 1 + Random(System.currentTimeMillis()).nextInt(100)

        queries.transaction {
            repeat(count) {
                queries.insertOrReplace(place())
            }
        }

        val item = place()
        val updatedAt = DateTime.parse(item.updatedAt).plusYears(5).toString()
        queries.insertOrReplace(item.copy(updatedAt = updatedAt))

        assert(queries.selectMaxUpdatedAt().executeAsOne().MAX == updatedAt)
    }

    private fun place() = Place.Impl(
        id = UUID.randomUUID().toString(),
        name = "Test",
        latitude = 50.0,
        longitude = 1.0,
        categoryId = UUID.randomUUID().toString(),
        description = "Test",
        phone = "1234",
        website = "www.test.com",
        openingHours = "24/7",
        visible = true,
        createdAt = DateTime.now().toString(),
        updatedAt = DateTime.now().toString()
    )
}