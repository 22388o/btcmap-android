package com.bubelov.coins.profile

import androidx.lifecycle.ViewModel
import com.bubelov.coins.repository.user.UserRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class ProfileViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    suspend fun getUser() = userRepository.getUser()

    suspend fun signOut() {
        userRepository.clear()
    }
}