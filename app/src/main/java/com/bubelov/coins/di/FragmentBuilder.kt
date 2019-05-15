package com.bubelov.coins.di

import com.bubelov.coins.auth.*
import com.bubelov.coins.editplace.EditPlaceFragment
import com.bubelov.coins.editplace.EditPlaceModule
import com.bubelov.coins.map.MapFragment
import com.bubelov.coins.map.MapModule
import com.bubelov.coins.notificationarea.NotificationAreaFragment
import com.bubelov.coins.notificationarea.NotificationAreaModule
import com.bubelov.coins.permissions.PermissionsFragment
import com.bubelov.coins.permissions.PermissionsModule
import com.bubelov.coins.picklocation.PickLocationFragment
import com.bubelov.coins.picklocation.PickLocationModule
import com.bubelov.coins.placedetails.PlaceDetailsFragment
import com.bubelov.coins.placedetails.PlaceDetailsModule
import com.bubelov.coins.profile.ProfileFragment
import com.bubelov.coins.profile.ProfileModule
import com.bubelov.coins.rates.ExchangeRatesFragment
import com.bubelov.coins.rates.ExchangeRatesModule
import com.bubelov.coins.search.PlacesSearchFragment
import com.bubelov.coins.search.PlacesSearchModule
import com.bubelov.coins.settings.SettingsFragment
import com.bubelov.coins.settings.SettingsModule
import com.bubelov.coins.support.SupportProjectFragment
import com.bubelov.coins.support.SupportProjectModule
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class FragmentBuilder {
    @ContributesAndroidInjector(modules = [MapModule::class])
    abstract fun contributeMapFragmentInjector(): MapFragment

    @ContributesAndroidInjector(modules = [AuthModule::class])
    abstract fun contributeAuthFragmentInjector(): AuthFragment

    @ContributesAndroidInjector(modules = [AuthModule::class])
    abstract fun contributeEmailAuthFragmentInjector(): EmailAuthFragment

    @ContributesAndroidInjector(modules = [AuthModule::class])
    abstract fun contributeEmailSignInFragmentInjector(): EmailSignInFragment

    @ContributesAndroidInjector(modules = [AuthModule::class])
    abstract fun contributeEmailSignUpFragmentInjector(): EmailSignUpFragment

    @ContributesAndroidInjector(modules = [ProfileModule::class])
    abstract fun contributeProfileFragmentInjector(): ProfileFragment

    @ContributesAndroidInjector(modules = [SettingsModule::class])
    abstract fun contributeSettingsFragmentInjector(): SettingsFragment

    @ContributesAndroidInjector(modules = [SupportProjectModule::class])
    abstract fun contributeSupportProjectFragmentInjector(): SupportProjectFragment

    @ContributesAndroidInjector(modules = [EditPlaceModule::class])
    abstract fun contributeEditPlaceFragmentInjector(): EditPlaceFragment

    @ContributesAndroidInjector(modules = [PickLocationModule::class])
    abstract fun contributePickLocationFragmentInjector(): PickLocationFragment

    @ContributesAndroidInjector(modules = [ExchangeRatesModule::class])
    abstract fun contributeExchangeRatesFragmentInjector(): ExchangeRatesFragment

    @ContributesAndroidInjector(modules = [PlacesSearchModule::class])
    abstract fun contributePlacesSearchFragmentInjector(): PlacesSearchFragment

    @ContributesAndroidInjector(modules = [NotificationAreaModule::class])
    abstract fun contributeNotificationAreaFragmentInjector(): NotificationAreaFragment

    @ContributesAndroidInjector(modules = [PlaceDetailsModule::class])
    abstract fun contributePlaceDetailsFragmentInjector(): PlaceDetailsFragment

    @ContributesAndroidInjector(modules = [PermissionsModule::class])
    abstract fun contributePermissionsFragmentInjector(): PermissionsFragment
}