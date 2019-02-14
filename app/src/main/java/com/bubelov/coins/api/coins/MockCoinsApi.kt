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
import com.bubelov.coins.model.Place
import com.bubelov.coins.model.PlaceCategory
import com.bubelov.coins.repository.currency.BuiltInCurrenciesCache
import com.bubelov.coins.repository.place.BuiltInPlacesCache
import com.bubelov.coins.repository.placecategory.BuiltInPlaceCategoriesCache
import kotlinx.coroutines.Deferred
import org.joda.time.DateTime
import retrofit2.mock.BehaviorDelegate
import java.util.*

class MockCoinsApi(
    private val delegate: BehaviorDelegate<CoinsApi>,
    currenciesCache: BuiltInCurrenciesCache,
    placesCache: BuiltInPlacesCache,
    placeCategoriesCache: BuiltInPlaceCategoriesCache
) : CoinsApi {
    private val currencies = mutableListOf<Currency>()
    private val places = mutableListOf<Place>()
    private val placeCategories = mutableListOf<PlaceCategory>()

    init {
        currencies.addAll(currenciesCache.getCurrencies())
        places.addAll(placesCache.getPlaces())
        placeCategories.addAll(placeCategoriesCache.getPlaceCategories())
    }

    override fun createUser(args: CreateUserArgs): Deferred<UserResponse> {
        val response = UserResponse(
            id = 1L,
            email = args.email,
            firstName = "Foo",
            lastName = "Bar",
            avatarUrl = "",
            god = false,
            createdAt = Date(),
            updatedAt = Date()
        )

        return delegate.returningResponse(response).createUser(args)
    }

    override fun getUser(id: Long, authorization: String): Deferred<UserResponse> {
        val response = UserResponse(
            id = 1L,
            email = "foo@bar.com",
            firstName = "Boo",
            lastName = "Bar",
            avatarUrl = "",
            god = false,
            createdAt = Date(),
            updatedAt = Date()
        )

        return delegate.returningResponse(response).getUser(id, authorization)
    }

    override fun createApiToken(authorization: String): Deferred<TokenResponse> {
        val response = TokenResponse(
            id = 1L,
            userId = 1L,
            token = UUID.randomUUID().toString(),
            createdAt = Date(),
            updatedAt = Date()
        )

        return delegate.returningResponse(response).createApiToken(authorization)
    }

    override fun getCurrencies(
        createdOrUpdatedAfter: DateTime,
        maxResults: Int
    ): Deferred<List<Currency>> {
        return delegate.returningResponse(emptyList<Currency>()).getCurrencies(
            createdOrUpdatedAfter,
            maxResults
        )
    }

    override fun getPlaces(
        createdOrUpdatedAfter: DateTime,
        maxResults: Int
    ): Deferred<List<Place>> {
        return delegate.returningResponse(emptyList<Place>()).getPlaces(
            createdOrUpdatedAfter,
            maxResults
        )
    }

    override fun createPlace(authorization: String, args: CreatePlaceArgs): Deferred<Place> {
        val place = args.place.copy(id = UUID.randomUUID().toString().hashCode().toLong())
        places += place
        return delegate.returningResponse(place).createPlace(authorization, args)
    }

    override fun updatePlace(id: Long, authorization: String, args: UpdatePlaceArgs): Deferred<Place> {
        val existingPlace = places.find { it.id == id }
        places.remove(existingPlace)
        places += args.place
        return delegate.returningResponse(args.place).updatePlace(id, authorization, args)
    }

    override fun getPlaceCategories(
        createdOrUpdatedAfter: DateTime,
        maxResults: Int
    ): Deferred<List<PlaceCategory>> {
        return delegate.returningResponse(emptyList<Place>()).getPlaceCategories(
            createdOrUpdatedAfter,
            maxResults
        )
    }
}