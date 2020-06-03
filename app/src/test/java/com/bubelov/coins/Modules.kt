package com.bubelov.coins

import android.content.Context
import android.content.res.AssetManager
import android.content.res.Resources
import android.location.LocationManager
import com.bubelov.coins.repository.LocationRepository
import com.bubelov.coins.repository.placeicon.PlaceIconsRepository
import com.nhaarman.mockitokotlin2.mock
import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver
import org.koin.dsl.module
import org.mockito.BDDMockito.given
import java.io.File

val mockAndroidModule = module(override = true) {
    single<Context> { mock() }

    single<Resources> { mock() }

    single<AssetManager> {
        mock {
            given(it.open("currencies.json"))
                .willReturn(File("./src/main/assets/currencies.json").inputStream())
            given(it.open("places.json"))
                .willReturn(File("./src/main/assets/places.json").inputStream())
            given(it.open("currencies_places.json"))
                .willReturn(File("./src/main/assets/currencies_places.json").inputStream())
            given(it.open("place_categories.json"))
                .willReturn(File("./src/main/assets/place_categories.json").inputStream())
        }
    }

    single<LocationManager> { mock() }

    single<SqlDriver> {
        JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY).apply {
            Database.Schema.create(this)
        }
    }

    single<LocationRepository> { mock() } // TODO remove

    single<PlaceIconsRepository> { mock() } // TODO remove
}