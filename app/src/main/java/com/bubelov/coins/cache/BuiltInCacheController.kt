package com.bubelov.coins.cache

import com.bubelov.coins.repository.currency.CurrenciesRepository
import com.bubelov.coins.repository.currencyplace.CurrenciesPlacesRepository
import com.bubelov.coins.repository.place.PlacesRepository
import com.bubelov.coins.repository.placecategory.PlaceCategoriesRepository

class BuiltInCacheController(
    private val currenciesRepository: CurrenciesRepository,
    private val placesRepository: PlacesRepository,
    private val currenciesPlacesRepository: CurrenciesPlacesRepository,
    private val placeCategoriesRepository: PlaceCategoriesRepository
) {
    suspend fun initBuiltInCaches() {
        currenciesRepository.initBuiltInCache()
        placesRepository.initBuiltInCache()
        currenciesPlacesRepository.initBuiltInCache()
        placeCategoriesRepository.initBuiltInCache()
    }
}