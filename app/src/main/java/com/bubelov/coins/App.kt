package com.bubelov.coins

import android.app.Application
import com.bubelov.coins.di.apiModule
import com.bubelov.coins.di.appModule
import com.bubelov.coins.di.mockApiModule
import com.bubelov.coins.sync.DatabaseSync
import com.bubelov.coins.sync.DatabaseSyncScheduler
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import timber.log.Timber

class App : Application() {

    private val databaseSyncScheduler: DatabaseSyncScheduler by inject()

    val databaseSync: DatabaseSync by inject()

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

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        databaseSyncScheduler.schedule()
    }
}