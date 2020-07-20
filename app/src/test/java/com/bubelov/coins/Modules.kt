package com.bubelov.coins

import android.content.Context
import android.content.res.AssetManager
import android.content.res.Resources
import android.location.LocationManager
import com.bubelov.coins.repository.LocationRepository
import com.bubelov.coins.repository.placeicon.PlaceIconsRepository
import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver
import org.koin.dsl.module
import org.mockito.BDDMockito.mock

val mockAndroidModule = module(override = true) {
    single<Context> { mock(Context::class.java) }

    single<Resources> { mock(Resources::class.java) }

    single<AssetManager> { mock(AssetManager::class.java) }

    single<LocationManager> { mock(LocationManager::class.java) }

    single<SqlDriver> {
        JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY).apply {
            Database.Schema.create(this)
        }
    }

    single<LocationRepository> { mock(LocationRepository::class.java) } // TODO remove

    single<PlaceIconsRepository> { mock(PlaceIconsRepository::class.java) } // TODO remove
}