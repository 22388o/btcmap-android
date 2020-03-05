package com.bubelov.coins.di

import android.content.Context
import com.bubelov.coins.Database
import com.squareup.sqldelight.android.AndroidSqliteDriver
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(context: Context): Database {
        val driver = AndroidSqliteDriver(
            schema = Database.Schema,
            context = context,
            name = "data.db"
        )

        return Database(driver)
    }
}