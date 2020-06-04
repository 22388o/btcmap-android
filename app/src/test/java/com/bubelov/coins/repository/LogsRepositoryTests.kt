package com.bubelov.coins.repository

import com.bubelov.coins.TestSuite
import com.bubelov.coins.repository.synclogs.LogsRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.koin.core.inject

class LogsRepositoryTests : TestSuite() {

    val repository: LogsRepository by inject()

    @Test
    fun selectAll_withEmptyDatabase_returnsEmptyList() = runBlocking {
        assertTrue(repository.getAll().first().isEmpty())
    }

    @Test
    fun append() = runBlocking {
        val message = "test_message"
        repository.append("test_tag", message)
        assertEquals(message, repository.getAll().first().first().message)
    }
}