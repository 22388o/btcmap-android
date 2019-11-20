package com.bubelov.coins.util

import androidx.lifecycle.MutableLiveData
import android.content.SharedPreferences

open class StringPreferenceLiveData(
    private val preferences: SharedPreferences,
    private val key: String,
    private val defaultValue: String = ""
) : MutableLiveData<String>(), SharedPreferences.OnSharedPreferenceChangeListener {
    var ignorePreferenceChange = false

    init {
        updateValue()
    }

    override fun onActive() = preferences.registerOnSharedPreferenceChangeListener(this)

    override fun onInactive() = preferences.unregisterOnSharedPreferenceChangeListener(this)

    override fun onSharedPreferenceChanged(preferences: SharedPreferences, key: String) {
        if (!ignorePreferenceChange) {
            updateValue()
        }
    }

    override fun setValue(value: String) {
        super.setValue(value)
        ignorePreferenceChange = true
        preferences.edit().putString(key, value).apply()
        ignorePreferenceChange = false
    }

    private fun updateValue() {
        setValue(preferences.getString(key, defaultValue)!!)
    }
}