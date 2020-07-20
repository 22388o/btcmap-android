package com.bubelov.coins

import com.bubelov.coins.data.Place
import org.joda.time.DateTime
import java.util.*

fun emptyPlace(): Place {
    return Place(
        id = UUID.randomUUID().toString(),
        source = "test",
        external_id = UUID.randomUUID().toString(),
        name = "",
        description = "",
        latitude = 0.0,
        longitude = 0.0,
        address = "",
        category = UUID.randomUUID().toString(),
        phone = "",
        website = "",
        opening_hours = "",
        valid = true,
        created_at = DateTime.now().toString(),
        updated_at = DateTime.now().toString()
    )
}