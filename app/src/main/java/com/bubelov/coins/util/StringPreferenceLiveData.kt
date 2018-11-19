/*
 * This is free and unencumbered software released into the public domain.
 *
 * Anyone is free to copy, modify, publish, use, compile, sell, or
 * distribute this software, either in source code form or as a compiled
 * binary, for any purpose, commercial or non-commercial, and by any
 * means.
 *
 * In jurisdictions that recognize copyright laws, the author or authors
 * of this software dedicate any and all copyright interest in the
 * software to the public domain. We make this dedication for the benefit
 * of the public at large and to the detriment of our heirs and
 * successors. We intend this dedication to be an overt act of
 * relinquishment in perpetuity of all present and future rights to this
 * software under copyright law.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS BE LIABLE FOR ANY CLAIM, DAMAGES OR
 * OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 *
 * For more information, please refer to <https://unlicense.org>
 */

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
        setValue(preferences.getString(key, defaultValue))
    }
}