package com.bubelov.coins.profile

import com.bubelov.coins.TestSuite
import com.bubelov.coins.model.User
import com.bubelov.coins.repository.user.UserRepository
import kotlinx.coroutines.runBlocking
import org.joda.time.DateTime
import org.junit.Test
import org.koin.core.inject
import org.koin.test.mock.declareMock
import java.util.*
import org.mockito.BDDMockito.*

class ProfileViewModelTests : TestSuite() {

    val model: ProfileViewModel by inject()

    @Test
    fun getUser() = runBlocking {
        val user = User(
            id = UUID.randomUUID().toString(),
            email = "test@test.test",
            emailConfirmed = true,
            firstName = "Test",
            lastName = "Data",
            avatarUrl = "",
            createdAt = DateTime.now(),
            updatedAt = DateTime.now()
        )

        val userRepository = declareMock<UserRepository> {
            given(getUser()).willReturn(user)
        }

        assert(model.getUser() == user)

        verify(userRepository).getUser()
        verifyNoMoreInteractions(userRepository)
    }

    @Test
    fun signOut() = runBlocking {
        val userRepository = declareMock<UserRepository>()
        model.signOut()
        verify(userRepository).clear()
        verifyNoMoreInteractions(userRepository)
    }
}