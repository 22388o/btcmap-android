package com.bubelov.coins

import com.bubelov.coins.data.Place
import com.bubelov.coins.injections.mainModule
import com.bubelov.coins.injections.mockApiModule
import com.bubelov.coins.repository.place.BuiltInPlacesCache
import org.junit.Test
import org.junit.experimental.categories.Category
import org.koin.dsl.module
import org.koin.test.AutoCloseKoinTest
import org.koin.test.category.CheckModuleTest
import org.koin.test.check.checkModules

@Category(CheckModuleTest::class)
class ModuleTests : AutoCloseKoinTest() {

    @Test
    fun checkModules() = checkModules {
        val placesCacheMock = module(override = true) {
            single<BuiltInPlacesCache> {
                object : BuiltInPlacesCache {
                    override fun loadPlaces(): List<Place> {
                        return emptyList()
                    }
                }
            }
        }

        modules(mainModule, mockAndroidModule, mockApiModule, placesCacheMock)
    }
}