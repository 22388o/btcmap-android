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

import com.bubelov.coins.model.*
import com.bubelov.coins.model.Currency
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

    private val date1 = DateTime.parse("2019-03-01T20:10:14+07:00")

    private val user1 = User(
        id = "E5B65104-60FF-4EE4-8A38-36144F479A93",
        email = "foo@bar.com",
        emailConfirmed = false,
        firstName = "Foo",
        lastName = "Bar",
        avatarUrl = "",
        createdAt = date1,
        updatedAt = date1
    )

    private val users = mutableListOf(user1)

    init {
        currencies.addAll(currenciesCache.getCurrencies())
        places.addAll(placesCache.getPlaces())
        currenciesPlaces.addAll(currenciesPlacesCache.getCurrenciesPlaces())
        placeCategories.addAll(placeCategoriesCache.getPlaceCategories())
    }

    override suspend fun addUser(args: CreateUserArgs): UserResponse {
        val user = User(
            id = UUID.randomUUID().toString(),
            email = args.email,
            emailConfirmed = false,
            firstName = args.firstName,
            lastName = args.lastName,
            avatarUrl = "",
            createdAt = DateTime.now(),
            updatedAt = DateTime.now()
        )

        users += user

        return UserResponse(
            id = user.id,
            email = user.email,
            emailConfirmed = user.emailConfirmed,
            firstName = user.firstName,
            lastName = user.lastName,
            avatarUrl = user.avatarUrl,
            createdAt = user.createdAt,
            updatedAt = user.updatedAt
        )
    }

    override suspend fun getUser(id: String, authorization: String): UserResponse {
        val user = users.firstOrNull { it.id == id } ?: throw Exception("No user with id: $id")

        return UserResponse(
            id = user.id,
            email = user.email,
            emailConfirmed = user.emailConfirmed,
            firstName = user.firstName,
            lastName = user.lastName,
            avatarUrl = user.avatarUrl,
            createdAt = user.createdAt,
            updatedAt = user.updatedAt
        )
    }

    override suspend fun getApiToken(authorization: String): TokenResponse {
        return TokenResponse(
            id = UUID.randomUUID().toString(),
            userId = user1.id,
            token = UUID.randomUUID().toString(),
            createdAt = DateTime.now(),
            updatedAt = DateTime.now()
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

    override suspend fun addPlace(authorization: String, args: CreatePlaceArgs): Place {
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