package com.bubelov.coins

import android.app.Application
import com.bubelov.coins.di.androidModule
import com.bubelov.coins.di.apiModule
import com.bubelov.coins.di.mainModule
import com.bubelov.coins.repository.synclogs.LogsRepository
import com.bubelov.coins.sync.DatabaseSync
import com.bubelov.coins.sync.DatabaseSyncScheduler
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class App : Application() {

    val databaseSync: DatabaseSync by inject()

    private val databaseSyncScheduler: DatabaseSyncScheduler by inject()

    private val log: LogsRepository by inject()

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@App)
            modules(listOf(mainModule, androidModule, apiModule))
        }

        log += "app.onCreate"

        GlobalScope.launch {
            databaseSync.sync()
            databaseSyncScheduler.schedule()
        }
    }
}