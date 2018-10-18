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

import com.bubelov.coins.auth.*
import com.bubelov.coins.editplace.EditPlaceFragment
import com.bubelov.coins.editplace.EditPlaceModule
import com.bubelov.coins.map.MapFragment
import com.bubelov.coins.map.MapModule
import com.bubelov.coins.notificationarea.NotificationAreaFragment
import com.bubelov.coins.notificationarea.NotificationAreaModule
import com.bubelov.coins.picklocation.PickLocationFragment
import com.bubelov.coins.picklocation.PickLocationModule
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
    abstract fun contributeAuthorizationOptionsFragmentInjector(): AuthOptionsFragment

    @ContributesAndroidInjector(modules = [AuthModule::class])
    abstract fun contributeEmailSignInFragmentInjector(): EmailSignInFragment

    @ContributesAndroidInjector(modules = [AuthModule::class])
    abstract fun contributeSignInFragmentInjector(): SignInFragment

    @ContributesAndroidInjector(modules = [AuthModule::class])
    abstract fun contributeSignUpFragmentInjector(): SignUpFragment

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
}