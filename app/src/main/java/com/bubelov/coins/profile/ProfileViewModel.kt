package com.bubelov.coins.profile

import androidx.lifecycle.ViewModel
import com.bubelov.coins.repository.user.UserRepository

class ProfileViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    fun getUser() = userRepository.getUser()

    fun signOut() {
        userRepository.clear()
    }
}