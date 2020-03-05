package com.bubelov.coins.db

import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import com.bubelov.coins.model.Place
import com.bubelov.coins.repository.place.PlacesDb
import com.bubelov.coins.util.transaction

@Database(
    entities = [
        Place::class
    ],
    version = 3,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class Database : RoomDatabase() {
    abstract fun placesDb(): PlacesDb

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.transaction {
                    execSQL("DROP TABLE IF EXISTS places")

                    execSQL(
                        "CREATE TABLE `Place` (" +
                                "`id` INTEGER NOT NULL, " +
                                "`name` TEXT NOT NULL, " +
                                "`latitude` REAL NOT NULL, " +
                                "`longitude` REAL NOT NULL, " +
                                "`category` TEXT NOT NULL, " +
                                "`description` TEXT NOT NULL, " +
                                "`currencies` TEXT NOT NULL, " +
                                "`openedClaims` INTEGER NOT NULL, " +
                                "`closedClaims` INTEGER NOT NULL, " +
                                "`phone` TEXT NOT NULL, " +
                                "`website` TEXT NOT NULL, " +
                                "`openingHours` TEXT NOT NULL, " +
                                "`visible` INTEGER NOT NULL, " +
                                "`updatedAt` INTEGER NOT NULL, " +
                                "PRIMARY KEY(`id`)" +
                                ")"
                    )
                }
            }
        }
    }
}