package api.coins

import model.User
import etc.Json
import db.Place
import retrofit2.http.*
import java.time.LocalDateTime

interface CoinsApi {

    @POST("tokens")
    suspend fun getToken(
        @Header("Authorization") authorization: String
    ): TokenResponse

    @POST("users")
    suspend fun createUser(
        @Json @Body args: CreateUserArgs
    ): User

    @GET("users/{id}")
    suspend fun getUser(
        @Path("id") id: String,
        @Header("Authorization") authorization: String
    ): User

    @GET("places")
    suspend fun getPlaces(
        @Query("created_or_updated_since") createdOrUpdatedAfter: LocalDateTime
    ): List<Place>

    @POST("places")
    suspend fun addPlace(
        @Header("Authorization") authorization: String,
        @Json @Body args: CreatePlaceArgs
    ): Place

    @PATCH("places/{id}")
    suspend fun updatePlace(
        @Path("id") id: String,
        @Header("Authorization") authorization: String,
        @Json @Body args: UpdatePlaceArgs
    ): Place
}