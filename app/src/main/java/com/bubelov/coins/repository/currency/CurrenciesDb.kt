package com.bubelov.coins.repository.currency

import androidx.room.*
import com.bubelov.coins.model.Currency
import org.joda.time.DateTime

@Dao
interface CurrenciesDb {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(places: List<Currency>)

    @Query("SELECT * FROM Currency")
    fun all(): List<Currency>

    @Query("SELECT * FROM Currency WHERE id = :id")
    fun find(id: String): Currency?

    @Query("SELECT COUNT(*) FROM Currency")
    fun count(): Int

    @Query("SELECT MAX(updatedAt) FROM Currency")
    fun maxUpdatedAt(): DateTime?
}