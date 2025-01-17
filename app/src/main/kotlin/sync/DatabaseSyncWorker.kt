package sync

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import etc.App
import kotlinx.coroutines.runBlocking

class DatabaseSyncWorker(
    context: Context,
    params: WorkerParameters
) : Worker(context, params) {

    override fun doWork() = runBlocking {
        try {
            (applicationContext as App).databaseSync.sync()
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}