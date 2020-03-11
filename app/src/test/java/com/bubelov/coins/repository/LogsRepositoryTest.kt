package com.bubelov.coins.repository

import com.bubelov.coins.data.LogEntry
import com.bubelov.coins.data.LogEntryQueries
import com.bubelov.coins.repository.synclogs.LogsRepository
import com.nhaarman.mockitokotlin2.*
import com.squareup.sqldelight.Query
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class LogsRepositoryTest {

    @Mock private lateinit var logEntryQueries: LogEntryQueries

    private lateinit var repository: LogsRepository

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        repository = LogsRepository(logEntryQueries)
    }

    @Test
    fun selectAll_withEmptyDatabase_returnsEmptyList() = runBlocking {
        val mockQuery = mock<Query<LogEntry>> {
            on { executeAsList() } doReturn emptyList()
        }

        whenever(logEntryQueries.selectAll()).thenReturn(mockQuery)

        Assert.assertTrue(repository.getAll().first().isEmpty())

        verify(logEntryQueries).selectAll()
        verifyNoMoreInteractions(logEntryQueries)
    }

    @Test
    fun append() = runBlocking {
        repository.append("test_tag", "test_message")

        verify(logEntryQueries).insert(any())
        verifyNoMoreInteractions(logEntryQueries)
    }
}