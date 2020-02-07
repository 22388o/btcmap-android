package com.bubelov.coins.repository.place

import androidx.lifecycle.LiveData
import androidx.room.*

import com.bubelov.coins.model.Place
import kotlinx.coroutines.flow.Flow
import org.joda.time.DateTime

@Dao
interface PlacesDb {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(places: List<Place>)

    @Query("SELECT * FROM Place")
    fun all(): List<Place>

    @Query("SELECT * FROM Place")
    fun allAsync(): LiveData<List<Place>>

    @Query("SELECT * FROM Place")
    fun allAsFlow(): Flow<List<Place>>

    @Query("SELECT * FROM Place WHERE id = :id LIMIT 1")
    fun find(id: String): Place?

    @Query(
        "SELECT * FROM Place WHERE " +
                "UPPER(name) LIKE '%' || UPPER(:query) || '%' " +
                "OR UPPER(description) LIKE '%' || UPPER(:query) || '%' " +
                "OR UPPER(phone) LIKE '%' || UPPER(:query) || '%' " +
                "OR UPPER(website) LIKE '%' || UPPER(:query) || '%'"
    )
    fun findBySearchQuery(query: String): List<Place>

    @Query("SELECT * FROM Place ORDER BY RANDOM() LIMIT 1")
    fun findRandom(): Place?

    @Query("SELECT COUNT(*) FROM Place")
    fun count(): Int

    @Query("SELECT MAX(updatedAt) FROM Place")
    fun maxUpdatedAt(): DateTime?

    @Update
    fun update(place: Place)
}