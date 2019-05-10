package com.bubelov.coins.auth

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.bubelov.coins.repository.user.UserRepository
import com.bubelov.coins.util.blockingObserve
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class AuthViewModelTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock private lateinit var userRepository: UserRepository
    private lateinit var model: AuthViewModel

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        model = AuthViewModel(userRepository, Dispatchers.Default)
    }

    @Test
    fun showProgressBar() = runBlocking {
        whenever(userRepository.signIn("test", "test")).then {
            Thread.sleep(500)
        }

        model.signIn("test", "test")
        assertTrue(model.showProgress.blockingObserve())
    }
}