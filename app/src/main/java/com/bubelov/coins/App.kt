package com.bubelov.coins

import android.app.Application
import com.bubelov.coins.di.apiModule
import com.bubelov.coins.di.appModule
import com.bubelov.coins.di.mockApiModule
import com.bubelov.coins.repository.synclogs.LogsRepository
import com.bubelov.coins.sync.DatabaseSync
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

@ExperimentalCoroutinesApi
class App : Application() {

    //private val databaseSyncScheduler: DatabaseSyncScheduler by inject()

    val databaseSync: DatabaseSync by inject()

    private val logsRepository: LogsRepository by inject()

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@App)

            if (BuildConfig.MOCK_API) {
                modules(listOf(appModule, mockApiModule))
            } else {
                modules(listOf(appModule, apiModule))
            }
        }

        runBlocking {
            logsRepository.append("app", "onCreate")
        }

        //databaseSyncScheduler.schedule() TODO
    }
}