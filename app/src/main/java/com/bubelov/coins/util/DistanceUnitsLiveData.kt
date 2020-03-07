package com.bubelov.coins.util

import android.content.SharedPreferences
import android.content.res.Resources
import com.bubelov.coins.R

class DistanceUnitsLiveData(
    preferences: SharedPreferences,
    resources: Resources
) : StringPreferenceLiveData(
    preferences,
    resources.getString(R.string.pref_distance_units_key),
    resources.getString(R.string.pref_distance_units_automatic)
)