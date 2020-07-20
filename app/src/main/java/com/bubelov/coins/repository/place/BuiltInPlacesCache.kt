package com.bubelov.coins.repository.place

import com.bubelov.coins.data.Place

interface BuiltInPlacesCache {

    fun loadPlaces(): List<Place>
}