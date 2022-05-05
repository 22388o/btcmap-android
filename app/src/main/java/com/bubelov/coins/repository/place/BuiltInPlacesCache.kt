package com.bubelov.coins.repository.place

import db.Place

interface BuiltInPlacesCache {

    fun loadPlaces(): List<Place>
}