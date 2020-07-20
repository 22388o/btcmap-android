package com.bubelov.coins

import android.app.Application
import com.bubelov.coins.di.androidModule
import com.bubelov.coins.di.mainModule
import com.bubelov.coins.di.mockApiModule
import com.bubelov.coins.repository.place.PlacesRepository
import com.bubelov.coins.repository.synclogs.LogsRepository
import com.bubelov.coins.sync.DatabaseSync
import kotlinx.coroutines.runBlocking
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class App : Application() {

    //private val databaseSyncScheduler: DatabaseSyncScheduler by inject()

    private val placesRepository: PlacesRepository by inject()

    val databaseSync: DatabaseSync by inject()

    private val logsRepository: LogsRepository by inject()

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@App)
            modules(listOf(mainModule, androidModule, mockApiModule))
        }

        runBlocking {
            logsRepository.append("app", "onCreate")
        }

        runBlocking {
            placesRepository.initBuiltInCache()
        }

        //databaseSyncScheduler.schedule() TODO
    }
}