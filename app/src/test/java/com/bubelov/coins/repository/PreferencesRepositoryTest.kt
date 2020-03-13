package com.bubelov.coins.repository

import com.bubelov.coins.data.Preference
import com.bubelov.coins.data.PreferenceQueries
import com.nhaarman.mockitokotlin2.*
import com.squareup.sqldelight.Query
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

@ExperimentalCoroutinesApi
class PreferencesRepositoryTest {

    @Mock private lateinit var queries: PreferenceQueries

    private lateinit var repository: PreferencesRepository

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        repository = PreferencesRepository(queries)
    }

    @Test
    fun put() = runBlocking {
        val key = "test_key"
        val value = "test_value"
        repository.put(key, value)
        verify(queries).insertOrReplace(Preference.Impl(key, value))
    }

    @Test
    fun get() = runBlocking {
        val key = "test_key"
        val value = "test_value"

        val mockQuery = mock<Query<Preference>> {
            on { executeAsOneOrNull() } doReturn Preference.Impl(key, value)
        }

        whenever(queries.selectByKey(key)).thenReturn(mockQuery)

        assert(repository.get(key).first() == value)

        verify(queries).selectByKey(key)
        verifyNoMoreInteractions(queries)
    }

    @Test
    fun all() = runBlocking {
        val preferences = listOf(
            Preference.Impl("test_key_1", "test_value_1"),
            Preference.Impl("test_key_2", "test_value_2")
        )

        val mockQuery = mock<Query<Preference>> {
            on { executeAsList() } doReturn preferences
        }

        whenever(queries.selectAll()).doReturn(mockQuery)

        assert(repository.getAll().first() == preferences)

        verify(queries).selectAll()
        verifyNoMoreInteractions(queries)
    }

    @Test
    fun count() = runBlocking {
        val count = 5.toLong()

        val mockQuery = mock<Query<Long>> {
            onGeneric { executeAsOne() } doReturn count
        }

        whenever(queries.selectCount()).thenReturn(mockQuery)

        assert(repository.getCount().first() == count)

        verify(queries).selectCount()
        verifyNoMoreInteractions(queries)
    }
}