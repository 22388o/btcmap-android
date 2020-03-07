package com.bubelov.coins.search

import androidx.lifecycle.ViewModel
import com.bubelov.coins.util.LiveEvent
import com.bubelov.coins.util.toSingleEvent

class PlacesSearchResultViewModel : ViewModel() {

    private val _pickedPlaceId = LiveEvent<String>()
    val pickedPlaceId = _pickedPlaceId.toSingleEvent()

    fun pickPlace(id: String) {
        _pickedPlaceId.value = id
    }
}