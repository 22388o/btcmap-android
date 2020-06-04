package com.bubelov.coins.repository

import com.bubelov.coins.TestSuite
import com.bubelov.coins.data.Preference
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import org.koin.core.inject

class PreferencesRepositoryTests : TestSuite() {

    val repository: PreferencesRepository by inject()

    @Test
    fun put() = runBlocking {
        val key = "test_key"
        val value = "test_value"
        repository.put(key, value)
        assertEquals(value, repository.get(key).first())
    }

    @Test
    fun all() = runBlocking {
        val preferences = listOf(
            Preference.Impl("test_key_1", "test_value_1"),
            Preference.Impl("test_key_2", "test_value_2")
        )

        preferences.forEach { repository.put(it.key, it.value) }

        assert(repository.getAll().first() == preferences)
    }

    @Test
    fun count() = runBlocking {
        val preferences = listOf(
            Preference.Impl("test_key_1", "test_value_1"),
            Preference.Impl("test_key_2", "test_value_2")
        )

        preferences.forEach { repository.put(it.key, it.value) }

        assert(repository.getCount().first() == preferences.size.toLong())
    }
}