package com.bubelov.coins

import com.bubelov.coins.di.mainModule
import com.bubelov.coins.di.mockApiModule
import org.junit.Test
import org.junit.experimental.categories.Category
import org.koin.test.AutoCloseKoinTest
import org.koin.test.category.CheckModuleTest
import org.koin.test.check.checkModules

@Category(CheckModuleTest::class)
class ModuleTests : AutoCloseKoinTest() {

    @Test
    fun checkModules() = checkModules {
        modules(mainModule, mockAndroidModule, mockApiModule)
    }
}