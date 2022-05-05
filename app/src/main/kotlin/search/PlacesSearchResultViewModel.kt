package search

import androidx.lifecycle.ViewModel
import etc.LiveEvent
import etc.toSingleEvent

class PlacesSearchResultViewModel : ViewModel() {

    private val _pickedPlaceId = LiveEvent<String>()
    val pickedPlaceId = _pickedPlaceId.toSingleEvent()

    fun pickPlace(id: String) {
        _pickedPlaceId.value = id
    }
}