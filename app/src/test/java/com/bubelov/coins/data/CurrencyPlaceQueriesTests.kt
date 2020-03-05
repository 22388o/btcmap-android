package com.bubelov.coins.data

import com.bubelov.coins.Database
import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver
import org.joda.time.DateTime
import org.junit.Before
import org.junit.Test
import java.util.*
import kotlin.random.Random

class CurrencyPlaceQueriesTests {

    lateinit var queries: CurrencyPlaceQueries

    @Before
    fun setUp() {
        val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        Database.Schema.create(driver)
        val database = Database(driver)
        queries = database.currencyPlaceQueries
    }

    @Test
    fun emptyByDefault() {
        assert(queries.selectCount().executeAsOne() == 0L)
    }

    @Test
    fun insertOrReplace_insertsData() {
        val item = currencyPlace()
        queries.insertOrReplace(item)

        assert(queries.selectCount().executeAsOne() == 1L)
        assert(queries.selectAll().executeAsOne() == item)
    }

    @Test
    fun insertOrReplace_replacesData() {
        val item = currencyPlace()
        queries.insertOrReplace(item)

        val updatedItem = item.copy(updatedAt = DateTime.now().plusMinutes(5).toString())
        queries.insertOrReplace(updatedItem)

        assert(queries.selectCount().executeAsOne() == 1L)
        assert(queries.selectAll().executeAsOne() == updatedItem)
    }

    @Test
    fun selectAll_selectsAll() {
        val items = listOf(currencyPlace(), currencyPlace())

        queries.transaction {
            items.forEach {
                queries.insertOrReplace(it)
            }
        }

        assert(queries.selectAll().executeAsList() == items)
    }

    @Test
    @ExperimentalStdlibApi
    fun selectByPlaceId_selectsCorrectItem() {
        val items = buildList<CurrencyPlace> {
            repeat(100) {
                add(currencyPlace())
            }
        }

        queries.transaction {
            items.forEach {
                queries.insertOrReplace(it)
            }
        }

        val randomItem = items.random()
        assert(queries.selectByPlaceId(randomItem.placeId).executeAsOne() == randomItem)
    }

    @Test
    @ExperimentalStdlibApi
    fun selectCount_returnsCorrectCount() {
        val count = 1 + Random(System.currentTimeMillis()).nextInt(100)

        queries.transaction {
            repeat(count) {
                queries.insertOrReplace(currencyPlace())
            }
        }

        assert(queries.selectCount().executeAsOne() == count.toLong())
    }

    @Test
    fun selectMaxUpdatedAt_selectsCorrectItem() {
        val count = 1 + Random(System.currentTimeMillis()).nextInt(100)

        queries.transaction {
            repeat(count) {
                queries.insertOrReplace(currencyPlace())
            }
        }

        val currency = currencyPlace()
        val updatedAt = DateTime.parse(currency.updatedAt).plusYears(5).toString()
        queries.insertOrReplace(currency.copy(updatedAt = updatedAt))

        assert(queries.selectMaxUpdatedAt().executeAsOne().MAX == updatedAt)
    }

    private fun currencyPlace() = CurrencyPlace.Impl(
        currencyId = UUID.randomUUID().toString(),
        placeId = UUID.randomUUID().toString(),
        createdAt = DateTime.now().toString(),
        updatedAt = DateTime.now().toString()
    )
}