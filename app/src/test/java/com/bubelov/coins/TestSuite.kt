package com.bubelov.coins

import android.content.res.AssetManager
import com.bubelov.coins.injections.mainModule
import com.bubelov.coins.injections.mockApiModule
import org.junit.Before
import org.junit.Rule
import org.koin.test.KoinTest
import org.koin.test.KoinTestRule
import org.koin.test.mock.MockProviderRule
import org.koin.test.mock.declareMock
import org.mockito.BDDMockito.given
import org.mockito.Mockito

open class TestSuite : KoinTest {

    @get:Rule
    val koinTestRule = KoinTestRule.create {
        modules(mainModule, mockAndroidModule, mockApiModule)
    }

    @get:Rule
    val mockProvider = MockProviderRule.create { clazz ->
        Mockito.mock(clazz.java)
    }

    @Before
    fun mockAndroidDependencies() {
        declareMock<AssetManager> {
            given(open("places.json"))
                .willReturn(java.io.File("./src/main/assets/places.json").inputStream())
        }
    }
}