package profile

import androidx.lifecycle.ViewModel
import repository.user.UserRepository

class ProfileViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    suspend fun getUser() = userRepository.getUser()

    suspend fun signOut() {
        userRepository.clear()
    }
}