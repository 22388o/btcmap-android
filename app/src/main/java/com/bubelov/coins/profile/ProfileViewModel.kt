package com.bubelov.coins.profile

import androidx.lifecycle.ViewModel
import com.bubelov.coins.repository.user.UserRepository

class ProfileViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    suspend fun getUser() = userRepository.getUser()

    suspend fun signOut() {
        userRepository.clear()
    }
}