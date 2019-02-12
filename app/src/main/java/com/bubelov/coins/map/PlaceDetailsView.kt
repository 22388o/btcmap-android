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

package com.bubelov.coins.map

import android.content.Context
import android.content.Intent
import android.graphics.Paint
import androidx.core.content.ContextCompat
import android.text.TextUtils
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.Toast
import androidx.core.view.isVisible
import com.bubelov.coins.R
import com.bubelov.coins.model.Place
import com.bubelov.coins.util.openUrl
import kotlinx.android.synthetic.main.widget_place_details.view.*
import kotlin.properties.Delegates

class PlaceDetailsView(context: Context, attrs: AttributeSet) : FrameLayout(context, attrs) {
    var place by Delegates.observable<Place?>(null) { _, _, newValue ->
        showPlace(newValue)
    }

    var fullScreen: Boolean = false
        set(fullScreen) {
            field = fullScreen
            mapHeaderShadow.isVisible = !fullScreen
            mapHeader.isVisible = !fullScreen
            placeToolbar.isVisible = fullScreen
        }

    var onDismissed: (() -> Unit)? = null

    init {
        View.inflate(context, R.layout.widget_place_details, this)

        placeToolbar.apply {
            setNavigationOnClickListener { onDismissed?.invoke() }
            inflateMenu(R.menu.place_details)

            setOnMenuItemClickListener { item ->
                val place = place ?: return@setOnMenuItemClickListener false

                when (item.itemId) {
                    R.id.action_share -> {
                        val subject = resources.getString(R.string.share_place_message_title)

                        val text = resources.getString(
                            R.string.share_place_message_text,
                            String.format(
                                "https://www.google.com/maps/@%s,%s,19z?hl=en",
                                place.latitude,
                                place.longitude
                            )
                        )

                        val intent = Intent(android.content.Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(android.content.Intent.EXTRA_SUBJECT, subject)
                            putExtra(android.content.Intent.EXTRA_TEXT, text)
                        }

                        context.startActivity(Intent.createChooser(intent, "Share"))

                        true
                    }
                    else -> false
                }
            }
        }
    }

    private fun showPlace(place: Place?) {
        if (place == null) {
            onDismissed?.invoke()
            return
        }

        //checkMark.isVisible = place.openedClaims > 0 && place.closedClaims == 0
        //warning.isVisible = place.closedClaims > 0

        if (TextUtils.isEmpty(place.name)) {
            name.setText(R.string.name_unknown)
            placeToolbar.setTitle(R.string.name_unknown)
        } else {
            name.text = place.name
            placeToolbar.title = place.name
        }

        if (TextUtils.isEmpty(place.description)) {
            description.isVisible = false
        } else {
            description.isVisible = true
            description.text = place.description
        }

        if (TextUtils.isEmpty(place.phone)) {
            phone.setText(R.string.not_provided)
        } else {
            phone.text = place.phone
        }

        website.apply {
            if (TextUtils.isEmpty(place.website)) {
                setText(R.string.not_provided)
                setTextColor(ContextCompat.getColor(context, R.color.black))
                paintFlags = website!!.paintFlags and Paint.UNDERLINE_TEXT_FLAG.inv()
                setOnClickListener(null)
            } else {
                text = place.website
                setTextColor(ContextCompat.getColor(context, R.color.primary_dark))
                paintFlags = website.paintFlags or Paint.UNDERLINE_TEXT_FLAG
                setOnClickListener {
                    if (!context.openUrl(place.website)) {
                        val text = "Can't open url: ${place.website}"
                        Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        if (TextUtils.isEmpty(place.openingHours)) {
            openingHours.setText(R.string.not_provided)
        } else {
            openingHours.text = place.openingHours
        }
    }
}