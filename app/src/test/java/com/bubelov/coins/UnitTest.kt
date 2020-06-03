package com.bubelov.coins

import com.bubelov.coins.di.mainModule
import com.bubelov.coins.di.mockApiModule
import org.junit.Rule
import org.koin.test.KoinTest
import org.koin.test.KoinTestRule
import org.koin.test.mock.MockProviderRule
import org.mockito.Mockito

open class UnitTest : KoinTest {

    @get:Rule
    val koinTestRule = KoinTestRule.create {
        modules(mainModule, mockAndroidModule, mockApiModule)
    }

    @get:Rule
    val mockProvider = MockProviderRule.create { clazz ->
        Mockito.mock(clazz.java)
    }
}