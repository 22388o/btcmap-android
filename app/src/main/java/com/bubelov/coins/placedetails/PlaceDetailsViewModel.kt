package com.bubelov.coins.placedetails

import androidx.lifecycle.ViewModel
import com.bubelov.coins.repository.currency.CurrenciesRepository
import com.bubelov.coins.repository.currencyplace.CurrenciesPlacesRepository

class PlaceDetailsViewModel(
    val currenciesRepository: CurrenciesRepository,
    val currenciesPlacesRepository: CurrenciesPlacesRepository
) : ViewModel()