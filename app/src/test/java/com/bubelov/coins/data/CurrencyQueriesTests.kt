package com.bubelov.coins.data

import com.bubelov.coins.Database
import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver
import org.joda.time.DateTime
import org.junit.Before
import org.junit.Test
import java.util.*
import kotlin.random.Random

class CurrencyQueriesTests {

    lateinit var queries: CurrencyQueries

    @Before
    fun setUp() {
        val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        Database.Schema.create(driver)
        val database = Database(driver)
        queries = database.currencyQueries
    }

    @Test
    fun emptyByDefault() {
        assert(queries.selectCount().executeAsOne() == 0L)
    }

    @Test
    fun insertOrReplace_insertsData() {
        val currency = currency()
        queries.insertOrReplace(currency)

        assert(queries.selectCount().executeAsOne() == 1L)
        assert(queries.selectById(currency.id).executeAsOne() == currency)
    }

    @Test
    fun insertOrReplace_replacesData() {
        val currency = currency()
        queries.insertOrReplace(currency)

        val updatedCurrency = currency.copy(name = "Changed")
        queries.insertOrReplace(updatedCurrency)

        assert(queries.selectCount().executeAsOne() == 1L)
        assert(queries.selectById(currency.id).executeAsOne() == updatedCurrency)
    }

    @Test
    fun selectAll_selectsAll() {
        val currencies = listOf(currency(), currency())

        queries.transaction {
            currencies.forEach {
                queries.insertOrReplace(it)
            }
        }

        assert(queries.selectAll().executeAsList() == currencies)
    }

    @Test
    @ExperimentalStdlibApi
    fun selectById_selectsCorrectItem() {
        val currencies = buildList<Currency> {
            repeat(100) {
                add(currency())
            }
        }

        queries.transaction {
            currencies.forEach {
                queries.insertOrReplace(it)
            }
        }

        val randomCurrency = currencies.random()

        assert(queries.selectById(randomCurrency.id).executeAsOne() == randomCurrency)
    }

    @Test
    @ExperimentalStdlibApi
    fun selectCount_returnsCorrectCount() {
        val count = 1 + Random(System.currentTimeMillis()).nextInt(100)

        queries.transaction {
            repeat(count) {
                queries.insertOrReplace(currency())
            }
        }

        assert(queries.selectCount().executeAsOne() == count.toLong())
    }

    @Test
    fun selectMaxUpdatedAt_selectsCorrectItem() {
        val count = 1 + Random(System.currentTimeMillis()).nextInt(100)

        queries.transaction {
            repeat(count) {
                queries.insertOrReplace(currency())
            }
        }

        val currency = currency()
        val updatedAt = DateTime.parse(currency.updatedAt).plusYears(5).toString()
        queries.insertOrReplace(currency.copy(updatedAt = updatedAt))

        assert(queries.selectMaxUpdatedAt().executeAsOne().MAX == updatedAt)
    }

    private fun currency() = Currency.Impl(
        id = UUID.randomUUID().toString(),
        name = "Test",
        code = "TST",
        crypto = true,
        createdAt = DateTime.now().toString(),
        updatedAt = DateTime.now().toString()
    )
}