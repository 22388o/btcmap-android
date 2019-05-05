package com.bubelov.coins.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bubelov.coins.auth.AuthResultViewModel
import com.bubelov.coins.auth.AuthViewModel
import com.bubelov.coins.editplace.EditPlaceViewModel
import com.bubelov.coins.map.MapViewModel
import com.bubelov.coins.notificationarea.NotificationAreaViewModel
import com.bubelov.coins.picklocation.PickLocationResultViewModel
import com.bubelov.coins.placedetails.PlaceDetailsViewModel
import com.bubelov.coins.rates.ExchangeRatesViewModel
import com.bubelov.coins.search.PlacesSearchResultViewModel
import com.bubelov.coins.search.PlacesSearchViewModel
import com.bubelov.coins.settings.SettingsViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(EditPlaceViewModel::class)
    internal abstract fun bindEditPlaceViewModel(dashboardViewModel: EditPlaceViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ExchangeRatesViewModel::class)
    internal abstract fun bindExchangeRatesViewModel(exchangeRatesViewModel: ExchangeRatesViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(MapViewModel::class)
    internal abstract fun bindMapViewModel(mapViewModel: MapViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(NotificationAreaViewModel::class)
    internal abstract fun bindNotificationAreaViewModel(notificationAreaViewModel: NotificationAreaViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(PlacesSearchViewModel::class)
    internal abstract fun bindPlacesSearchViewModel(placesSearchViewModel: PlacesSearchViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(PlacesSearchResultViewModel::class)
    internal abstract fun bindPlacesSearchResultViewModel(placesSearchResultViewModel: PlacesSearchResultViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SettingsViewModel::class)
    internal abstract fun bindSettingsViewModel(settingsViewModel: SettingsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(AuthViewModel::class)
    internal abstract fun bindAuthViewModel(authViewModel: AuthViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(AuthResultViewModel::class)
    internal abstract fun bindAuthResultViewModel(authResultViewModel: AuthResultViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(PickLocationResultViewModel::class)
    internal abstract fun bindPickLocationResultViewModel(pickLocationResultViewModel: PickLocationResultViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(PlaceDetailsViewModel::class)
    internal abstract fun bindPlaceDetailsViewModel(placeDetailsViewModel: PlaceDetailsViewModel): ViewModel

    @Binds
    internal abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory
}