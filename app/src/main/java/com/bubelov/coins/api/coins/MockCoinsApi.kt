package com.bubelov.coins.api.coins

import com.bubelov.coins.data.Place
import com.bubelov.coins.model.User
import com.bubelov.coins.repository.place.BuiltInPlacesCache
import com.bubelov.coins.repository.synclogs.LogsRepository
import kotlinx.coroutines.runBlocking
import org.joda.time.DateTime
import java.util.*

class MockCoinsApi(
    placesCache: BuiltInPlacesCache,
    logsRepository: LogsRepository
) : CoinsApi {

    private val places = mutableListOf<Place>()

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
        runBlocking {
            logsRepository.append("api", "Initializing mock API")
            places.addAll(placesCache.loadPlaces())
        }
    }

    override suspend fun getToken(authorization: String): TokenResponse {
        return TokenResponse(
            token = UUID.randomUUID().toString(),
            user = user1
        )
    }

    override suspend fun createUser(args: CreateUserArgs): User {
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
        return user
    }

    override suspend fun getUser(id: String, authorization: String): User {
        return users.firstOrNull { it.id == id } ?: throw Exception("No user with id: $id")
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
}