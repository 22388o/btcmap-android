package com.bubelov.coins.placedetails

import androidx.lifecycle.ViewModel
import com.bubelov.coins.repository.currency.CurrenciesRepository
import com.bubelov.coins.repository.currencyplace.CurrenciesPlacesRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class PlaceDetailsViewModel @Inject constructor(
    val currenciesRepository: CurrenciesRepository,
    val currenciesPlacesRepository: CurrenciesPlacesRepository,
    coroutineContext: CoroutineContext
) : ViewModel() {

    private val job = Job()
    private val uiScope = CoroutineScope(coroutineContext + job)

    override fun onCleared() {
        job.cancel()
        super.onCleared()
    }
}