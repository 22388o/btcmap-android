package com.bubelov.coins

import com.bubelov.coins.di.DaggerAppComponent
import com.bubelov.coins.sync.DatabaseSync
import com.bubelov.coins.sync.DatabaseSyncScheduler
import com.bubelov.coins.util.CrashlyticsTree
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import dagger.android.HasActivityInjector
import dagger.android.HasServiceInjector
import timber.log.Timber
import javax.inject.Inject

class App : DaggerApplication(), HasActivityInjector, HasServiceInjector {
    @Inject lateinit var databaseSyncScheduler: DatabaseSyncScheduler
    @Inject lateinit var databaseSync: DatabaseSync

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        } else {
            Timber.plant(CrashlyticsTree())
        }

        databaseSyncScheduler.schedule()
    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return DaggerAppComponent.builder().create(this)
    }
}