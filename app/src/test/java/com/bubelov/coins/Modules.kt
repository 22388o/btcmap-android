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
import org.koin.test.mock.declareMock
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.mock
import java.io.File

val mockAndroidModule = module(override = true) {
    single<Context> { mock(Context::class.java) }

    single<Resources> { mock(Resources::class.java) }

    single<AssetManager> {
        mock(AssetManager::class.java)
//        declareMock {
//            given(open("currencies.json"))
//                .willReturn(File("./src/main/assets/currencies.json").inputStream())
//            given(open("places.json"))
//                .willReturn(File("./src/main/assets/places.json").inputStream())
//            given(open("currencies_places.json"))
//                .willReturn(File("./src/main/assets/currencies_places.json").inputStream())
//            given(open("place_categories.json"))
//                .willReturn(File("./src/main/assets/place_categories.json").inputStream())
//        }
    }

    single<LocationManager> { mock(LocationManager::class.java) }

    single<SqlDriver> {
        JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY).apply {
            Database.Schema.create(this)
        }
    }

    single<LocationRepository> { mock(LocationRepository::class.java) } // TODO remove

    single<PlaceIconsRepository> { mock(PlaceIconsRepository::class.java) } // TODO remove
}