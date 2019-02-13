/*
 * This is free and unencumbered software released into the public domain.
 *
 * Anyone is free to copy, modify, publish, use, compile, sell, or
 * distribute this software, either in source code form or as a compiled
 * binary, for any purpose, commercial or non-commercial, and by any
 * means.
 *
 * In jurisdictions that recognize copyright laws, the author or authors
 * of this software dedicate any and all copyright interest in the
 * software to the public domain. We make this dedication for the benefit
 * of the public at large and to the detriment of our heirs and
 * successors. We intend this dedication to be an overt act of
 * relinquishment in perpetuity of all present and future rights to this
 * software under copyright law.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS BE LIABLE FOR ANY CLAIM, DAMAGES OR
 * OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 *
 * For more information, please refer to <https://unlicense.org>
 */

package com.bubelov.coins

import com.bubelov.coins.di.DaggerAppComponent
import com.bubelov.coins.sync.DatabaseSync
import com.bubelov.coins.sync.DatabaseSyncScheduler
import com.bubelov.coins.util.CrashlyticsTree
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import dagger.android.HasActivityInjector
import dagger.android.HasServiceInjector
import net.danlew.android.joda.JodaTimeAndroid
import timber.log.Timber
import javax.inject.Inject

class App : DaggerApplication(), HasActivityInjector, HasServiceInjector {
    @Inject lateinit var databaseSyncScheduler: DatabaseSyncScheduler
    @Inject lateinit var databaseSync: DatabaseSync

    override fun onCreate() {
        super.onCreate()

        JodaTimeAndroid.init(this)

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