package etc

import android.app.Application
import injections.module
import repository.synclogs.LogsRepository
import repository.synclogs.logEntry
import sync.DatabaseSync
import sync.DatabaseSyncScheduler
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
            modules(listOf(module))
        }

        log += logEntry("onCreate")

        GlobalScope.launch {
            databaseSync.sync()
            databaseSyncScheduler.schedule()
        }
    }
}