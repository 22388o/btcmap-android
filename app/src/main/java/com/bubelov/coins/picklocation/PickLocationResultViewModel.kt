package com.bubelov.coins.picklocation

import androidx.lifecycle.ViewModel
import com.bubelov.coins.model.Location
import com.bubelov.coins.util.LiveEvent
import com.bubelov.coins.util.toSingleEvent

class PickLocationResultViewModel : ViewModel() {
    private val _pickedLocation = LiveEvent<Location>()
    val pickedLocation = _pickedLocation.toSingleEvent()

    fun pickLocation(location: Location) {
        _pickedLocation.value = location
    }
}