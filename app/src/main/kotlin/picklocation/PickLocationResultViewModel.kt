package picklocation

import androidx.lifecycle.ViewModel
import model.Location
import etc.LiveEvent
import etc.toSingleEvent

class PickLocationResultViewModel : ViewModel() {
    private val _pickedLocation = LiveEvent<Location>()
    val pickedLocation = _pickedLocation.toSingleEvent()

    fun pickLocation(location: Location) {
        _pickedLocation.value = location
    }
}