package com.bubelov.coins.profile

import com.bubelov.coins.model.User
import com.bubelov.coins.repository.user.UserRepository
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import com.nhaarman.mockitokotlin2.whenever
import org.joda.time.DateTime
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import java.util.*

class ProfileViewModelTest {
    @Mock private lateinit var userRepository: UserRepository

    private lateinit var model: ProfileViewModel

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        model = ProfileViewModel(userRepository)
    }

    @Test
    fun getUser() {
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

        whenever(userRepository.getUser()).thenReturn(user)

        assert(model.getUser() == user)

        verify(userRepository).getUser()
        verifyNoMoreInteractions(userRepository)
    }

    @Test
    fun signOut() {
        model.signOut()
        verify(userRepository).clear()
        verifyNoMoreInteractions(userRepository)
    }
}