package com.bubelov.coins.auth

import com.bubelov.coins.TestSuite
import com.bubelov.coins.repository.user.UserRepository
import com.bubelov.coins.util.BasicTaskState
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import org.koin.test.inject
import org.koin.test.mock.declareMock
import org.mockito.BDDMockito.given

class AuthViewModelTests : TestSuite() {

    private val model: AuthViewModel by inject()

    @Test
    fun signIn_firstStateIsProgress() = runBlocking {
        val firstState = model.signIn("test", "test").first()
        assertEquals(BasicTaskState.Progress.javaClass, firstState.javaClass)
    }

    @Test
    fun signIn_lastStateIsSuccess_whenAllFine() = runBlocking {
        declareMock<UserRepository> {
            given(signIn("test", "test")).willReturn(Unit)
        }

        val lastState = model.signIn("test", "test")
            .toList()
            .last()

        assertEquals(BasicTaskState.Success.javaClass, lastState.javaClass)
    }

    @Test
    fun signIn_lastStateIsError_whenExceptionOccurs() = runBlocking {
        val exception = Exception("Test exception")

        declareMock<UserRepository> {
            given(signIn("test", "test")).will { throw  exception }
        }

        val lastState = model.signIn("test", "test")
            .toList()
            .last()

        assertEquals(BasicTaskState.Error("").javaClass, lastState.javaClass)
    }
}