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

package com.bubelov.coins.api.coins

import com.bubelov.coins.model.Currency
import com.bubelov.coins.model.CurrencyPlace
import com.bubelov.coins.model.Place
import com.bubelov.coins.model.PlaceCategory
import com.bubelov.coins.repository.currency.BuiltInCurrenciesCache
import com.bubelov.coins.repository.currencyplace.BuiltInCurrenciesPlacesCache
import com.bubelov.coins.repository.place.BuiltInPlacesCache
import com.bubelov.coins.repository.placecategory.BuiltInPlaceCategoriesCache
import org.joda.time.DateTime
import java.util.*

class MockCoinsApi(
    currenciesCache: BuiltInCurrenciesCache,
    placesCache: BuiltInPlacesCache,
    currenciesPlacesCache: BuiltInCurrenciesPlacesCache,
    placeCategoriesCache: BuiltInPlaceCategoriesCache
) : CoinsApi {
    private val currencies = mutableListOf<Currency>()
    private val places = mutableListOf<Place>()
    private val currenciesPlaces = mutableListOf<CurrencyPlace>()
    private val placeCategories = mutableListOf<PlaceCategory>()

    init {
        currencies.addAll(currenciesCache.getCurrencies())
        places.addAll(placesCache.getPlaces())
        currenciesPlaces.addAll(currenciesPlacesCache.getCurrenciesPlaces())
        placeCategories.addAll(placeCategoriesCache.getPlaceCategories())
    }

    override suspend fun createUser(args: CreateUserArgs): UserResponse {
        return UserResponse(
            id = 1L,
            email = args.email,
            firstName = "Foo",
            lastName = "Bar",
            avatarUrl = "",
            god = false,
            createdAt = Date(),
            updatedAt = Date()
        )
    }

    override suspend fun getUser(id: String, authorization: String): UserResponse {
        return UserResponse(
            id = 1L,
            email = "foo@bar.com",
            firstName = "Boo",
            lastName = "Bar",
            avatarUrl = "",
            god = false,
            createdAt = Date(),
            updatedAt = Date()
        )
    }

    override suspend fun createApiToken(authorization: String): TokenResponse {
        return TokenResponse(
            id = 1L,
            userId = UUID.randomUUID().toString(),
            token = UUID.randomUUID().toString(),
            createdAt = Date(),
            updatedAt = Date()
        )
    }

    override suspend fun getCurrencies(createdOrUpdatedAfter: DateTime): List<Currency> {
        return emptyList()
    }

    override suspend fun getCurrenciesPlaces(createdOrUpdatedAfter: DateTime): List<CurrencyPlace> {
        return emptyList()
    }

    override suspend fun getPlaces(createdOrUpdatedAfter: DateTime): List<Place> {
        return emptyList()
    }

    override suspend fun createPlace(authorization: String, args: CreatePlaceArgs): Place {
        places += args.place
        return args.place
    }

    override suspend fun updatePlace(
        id: String,
        authorization: String,
        args: UpdatePlaceArgs
    ): Place {
        val existingPlace = places.find { it.id == id }
        places.remove(existingPlace)
        places += args.place
        return args.place
    }

    override suspend fun getPlaceCategories(createdOrUpdatedAfter: DateTime): List<PlaceCategory> {
        return emptyList()
    }
}