package repository.place

import db.Place

interface BuiltInPlacesCache {

    fun loadPlaces(): List<Place>
}