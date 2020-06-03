package com.bubelov.coins.repository

import com.bubelov.coins.Database
import com.bubelov.coins.api.coins.CoinsApi
import com.bubelov.coins.data.PlaceQueries
import com.bubelov.coins.repository.place.BuiltInPlacesCache
import com.bubelov.coins.repository.place.PlacesRepository
import com.bubelov.coins.repository.synclogs.LogsRepository
import com.bubelov.coins.repository.user.UserRepository
import com.nhaarman.mockitokotlin2.*
import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import java.util.*

class PlacesRepositoryTest {

    @Mock private lateinit var api: CoinsApi
    @Mock private lateinit var placesAssetsCache: BuiltInPlacesCache
    @Mock private lateinit var userRepository: UserRepository
    @Mock private lateinit var logsRepository: LogsRepository

    private lateinit var queries: PlaceQueries

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)

        val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        Database.Schema.create(driver)
        val database = Database(driver)
        queries = database.placeQueries
    }

    @Test
    fun doNotUseAssetsCacheWhenEmpty() = runBlocking {
        val repository = PlacesRepository(
            api,
            queries,
            placesAssetsCache,
            userRepository,
            logsRepository
        )

        repository.find(UUID.randomUUID().toString())

        verifyZeroInteractions(placesAssetsCache)
        Unit
    }
}