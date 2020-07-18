package com.bubelov.coins

import com.bubelov.coins.data.Place
import org.joda.time.DateTime
import java.util.*

fun emptyPlace(): Place {
    return Place(
        id = UUID.randomUUID().toString(),
        name = "",
        latitude = 0.0,
        longitude = 0.0,
        categoryId = UUID.randomUUID().toString(),
        description = "",
        phone = "",
        website = "",
        openingHours = "",
        visible = true,
        createdAt = DateTime.now().toString(),
        updatedAt = DateTime.now().toString()
    )
}