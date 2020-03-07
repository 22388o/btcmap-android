package com.bubelov.coins.auth

import com.bubelov.coins.repository.user.UserRepository
import com.bubelov.coins.util.BasicTaskState
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import java.lang.Exception

class AuthViewModelTest {

    @Mock
    private lateinit var userRepository: UserRepository

    private lateinit var model: AuthViewModel

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        model = AuthViewModel(userRepository)
    }

    @Test
    fun signIn_firstStateIsProgress() = runBlocking {
        val firstState = model.signIn("test", "test").first()
        assert(firstState is BasicTaskState.Progress)
    }

    @Test
    fun signIn_lastStateIsSuccess_whenAllFine() = runBlocking {
        val lastState = model.signIn("test", "test")
            .toList()
            .last()

        assert(lastState is BasicTaskState.Success)
    }

    @Test
    fun signIn_lastStateIsError_whenExceptionOccurs() = runBlocking {
        val exception = Exception("Test exception")

        whenever(userRepository.signIn("test", "test")).then {
            throw exception
        }

        val lastState = model.signIn("test", "test")
            .toList()
            .last()

        assert(lastState is BasicTaskState.Error && lastState.message == exception.message)
    }
}