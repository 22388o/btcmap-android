package com.bubelov.coins.placedetails

import androidx.lifecycle.ViewModel
import com.bubelov.coins.repository.currency.CurrenciesRepository
import com.bubelov.coins.repository.currencyplace.CurrenciesPlacesRepository
import kotlin.time.ExperimentalTime

@ExperimentalTime
class PlaceDetailsViewModel(
    val currenciesRepository: CurrenciesRepository,
    val currenciesPlacesRepository: CurrenciesPlacesRepository
) : ViewModel()