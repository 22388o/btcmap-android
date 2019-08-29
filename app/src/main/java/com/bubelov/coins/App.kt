package com.bubelov.coins

import android.app.Application
import com.bubelov.coins.di.DaggerAppComponent
import com.bubelov.coins.sync.DatabaseSync
import com.bubelov.coins.sync.DatabaseSyncScheduler
import com.bubelov.coins.util.CrashlyticsTree
import dagger.android.*
import timber.log.Timber
import javax.inject.Inject

class App : Application(), HasAndroidInjector {

    @Inject
    lateinit var androidInjector: DispatchingAndroidInjector<Any>

    @Inject
    lateinit var databaseSyncScheduler: DatabaseSyncScheduler

    @Inject
    lateinit var databaseSync: DatabaseSync

    override fun onCreate() {
        super.onCreate()

        DaggerAppComponent.factory().create(this).inject(this)

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        } else {
            Timber.plant(CrashlyticsTree())
        }

        databaseSyncScheduler.schedule()
    }

    override fun androidInjector() = androidInjector
}