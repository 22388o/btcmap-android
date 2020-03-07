package com.bubelov.coins.auth

import androidx.lifecycle.ViewModel
import com.bubelov.coins.util.LiveEvent
import com.bubelov.coins.util.toSingleEvent

class AuthResultViewModel : ViewModel() {
    private val _authorized = LiveEvent<Unit>()
    val authorized = _authorized.toSingleEvent()

    fun onAuthSuccess() {
        _authorized.call()
    }
}