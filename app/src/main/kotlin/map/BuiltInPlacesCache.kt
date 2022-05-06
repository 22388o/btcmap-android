package map

import db.Place

interface BuiltInPlacesCache {

    fun loadPlaces(): List<Place>
}