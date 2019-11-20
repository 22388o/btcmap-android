package com.bubelov.coins.sync

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DatabaseSyncScheduler @Inject constructor() {
    fun schedule() {
//        val lifecycleOwner = ProcessLifecycleOwner.get()
//
//        WorkManager.getInstance().getWorkInfosByTagLiveData(TAG).observe(lifecycleOwner, Observer { info ->
//            Timber.d("Work info: $info")
//
//            if (info.isNullOrEmpty()) {
//                val constraints = Constraints.Builder()
//                    .setRequiresBatteryNotLow(true)
//                    .build()
//
//                val databaseSyncWork = PeriodicWorkRequestBuilder<DatabaseSyncWorker>(12, TimeUnit.HOURS)
//                    .setConstraints(constraints)
//                    .addTag(TAG)
//                    .build()
//
//                WorkManager.getInstance().enqueue(databaseSyncWork)
//            }
//        })
    }

    companion object {
        private const val TAG = "databaseSync"
    }
}