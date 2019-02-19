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
import com.bubelov.coins.util.Json
import kotlinx.coroutines.Deferred
import org.joda.time.DateTime
import retrofit2.http.*

interface CoinsApi {
    @POST("users")
    fun createUser(
        @Json @Body args: CreateUserArgs
    ): Deferred<UserResponse>

    @GET("users/{id}")
    fun getUser(
        @Path("id") id: Long,
        @Header("Authorization") authorization: String
    ): Deferred<UserResponse>

    @POST("tokens")
    fun createApiToken(
        @Header("Authorization") authorization: String
    ): Deferred<TokenResponse>

    @GET("currencies")
    fun getCurrencies(
        @Query("createdOrUpdatedAfter") createdOrUpdatedAfter: DateTime,
        @Query("maxResults") maxResults: Int
    ): Deferred<List<Currency>>

    @GET("currenciesPlaces")
    fun getCurrenciesPlaces(
        @Query("createdOrUpdatedAfter") createdOrUpdatedAfter: DateTime,
        @Query("maxResults") maxResults: Int
    ): Deferred<List<CurrencyPlace>>

    @GET("places")
    fun getPlaces(
        @Query("createdOrUpdatedAfter") createdOrUpdatedAfter: DateTime,
        @Query("maxResults") maxResults: Int
    ): Deferred<List<Place>>

    @POST("places")
    fun createPlace(
        @Header("Authorization") authorization: String,
        @Json @Body args: CreatePlaceArgs
    ): Deferred<Place>

    @PATCH("places/{id}")
    fun updatePlace(
        @Path("id") id: Long,
        @Header("Authorization") authorization: String,
        @Json @Body args: UpdatePlaceArgs
    ): Deferred<Place>

    @GET("placeCategories")
    fun getPlaceCategories(
        @Query("createdOrUpdatedAfter") createdOrUpdatedAfter: DateTime,
        @Query("maxResults") maxResults: Int
    ): Deferred<List<PlaceCategory>>
}