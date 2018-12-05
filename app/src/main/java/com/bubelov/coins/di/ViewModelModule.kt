/*
 * This is free and unencumbered software released into the public domain.
 *
 * Anyone is free to copy, modify, publish, use, compile, sell, or
 * distribute this software, either in source code form or as a compiled
 * binary, for any purpose, commercial or non-commercial, and by any
 * means.
 *
 * In jurisdictions that recognize copyright laws, the author or authors
 * of this software dedicate any and all copyright interest in the
 * software to the public domain. We make this dedication for the benefit
 * of the public at large and to the detriment of our heirs and
 * successors. We intend this dedication to be an overt act of
 * relinquishment in perpetuity of all present and future rights to this
 * software under copyright law.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS BE LIABLE FOR ANY CLAIM, DAMAGES OR
 * OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 *
 * For more information, please refer to <https://unlicense.org>
 */

package com.bubelov.coins.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bubelov.coins.auth.AuthResultViewModel
import com.bubelov.coins.auth.AuthViewModel
import com.bubelov.coins.editplace.EditPlaceViewModel
import com.bubelov.coins.map.MapViewModel
import com.bubelov.coins.notificationarea.NotificationAreaViewModel
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
    internal abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory
}