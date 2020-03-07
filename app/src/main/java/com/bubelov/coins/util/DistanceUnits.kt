package com.bubelov.coins.util

import java.util.*

enum class DistanceUnits {
    KILOMETERS,
    MILES;

    companion object {
        val default = Locale.getDefault().toDistanceUnits()

        private fun Locale.toDistanceUnits() = when(country) {
            in listOf("LR", "MM", "GB", "US") -> MILES
            else -> KILOMETERS
        }
    }
}