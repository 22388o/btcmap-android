package com.bubelov.coins.model

data class SyncLogEntry(
    val time: Long,
    val affectedPlaces: Int
)