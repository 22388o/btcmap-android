package com.bubelov.coins.di

import androidx.room.Room
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
    fun provideDatabase(context: Context) =
        Room.databaseBuilder(context, com.bubelov.coins.db.Database::class.java, "db.sqlite3")
            .apply {
                addMigrations(com.bubelov.coins.db.Database.MIGRATION_1_2)
            }.build()

    @Provides
    @Singleton
    fun provideNewDatabase(context: Context): Database {
        val driver = AndroidSqliteDriver(
            schema = Database.Schema,
            context = context,
            name = "data.db"
        )

        return Database(driver)
    }
}