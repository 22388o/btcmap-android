package com.bubelov.coins.data

import com.bubelov.coins.TestSuite
import org.joda.time.DateTime
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.koin.test.inject
import java.util.*
import kotlin.random.Random

class CurrencyQueriesTests : TestSuite() {

    private val queries: CurrencyQueries by inject()

    @Test
    fun insertOrReplace_insertsItem() {
        currency().apply {
            queries.insertOrReplace(this)
            assertEquals(this, queries.select(id).executeAsOneOrNull())
        }
    }

    @Test
    fun insertOrReplace_replacesItem_withNoDuplication() {
        currency().apply {
            queries.insertOrReplace(this)
            val updatedItem = copy(name = "Changed")
            queries.insertOrReplace(updatedItem)
            assertEquals(updatedItem, queries.select(id).executeAsOneOrNull())
            assertEquals(1L, queries.selectCount().executeAsOne())
        }
    }

    @Test
    fun selectAll_selectsAllItems() {
        listOf(currency(), currency()).apply {
            queries.transaction { forEach { queries.insertOrReplace(it) } }
            assertEquals(this, queries.selectAll().executeAsList())
        }
    }

    @Test
    @ExperimentalStdlibApi
    fun select_selectsCorrectItem() = queries.transaction {
        buildList<Currency> {
            repeat(100) { add(currency()) }
        }.apply {
            forEach { queries.insertOrReplace(it) }

            random().apply {
                assertEquals(this, queries.select(id).executeAsOne())
            }
        }
    }

    @Test
    @ExperimentalStdlibApi
    fun selectCount_returnsCorrectCount() = queries.transaction {
        val count = 1 + Random(System.currentTimeMillis()).nextInt(100)
        repeat(count) { queries.insertOrReplace(currency()) }
        assertEquals(count.toLong(), queries.selectCount().executeAsOne())
    }

    @Test
    fun selectMaxUpdatedAt_selectsCorrectItem() = queries.transaction {
        val count = 1 + Random(System.currentTimeMillis()).nextInt(5)
        repeat(count) { queries.insertOrReplace(currency()) }

        currency().apply {
            val updatedAt = DateTime.parse(updatedAt).plusYears(5).toString()
            queries.insertOrReplace(copy(updatedAt = updatedAt))
            assertEquals(updatedAt, queries.selectMaxUpdatedAt().executeAsOne().MAX)
        }
    }

    @Test
    fun updateName_updatesName() {
        currency().apply {
            queries.insertOrReplace(this)
            val newName = "new name"
            queries.updateName(newName, id)
            assertEquals(newName, queries.select(id).executeAsOne().name)
        }
    }

    @Test
    fun delete_deletesItem() {
        currency().apply {
            queries.insertOrReplace(this)
            queries.delete(id)
            assertNull(queries.select(id).executeAsOneOrNull())
        }
    }

    private fun currency() = Currency.Impl(
        id = UUID.randomUUID().toString(),
        name = "Test Coin",
        code = "TST",
        crypto = true,
        createdAt = DateTime.now().toString(),
        updatedAt = DateTime.now().toString()
    )
}