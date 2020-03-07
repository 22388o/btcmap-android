package com.bubelov.coins.placedetails

import android.animation.ArgbEvaluator
import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.bubelov.coins.R
import com.bubelov.coins.data.Place
import com.bubelov.coins.util.openUrl
import kotlinx.android.synthetic.main.fragment_place_details.*
import kotlinx.coroutines.runBlocking
import org.koin.android.viewmodel.ext.android.viewModel
import timber.log.Timber

class PlaceDetailsFragment : Fragment() {

    private val model: PlaceDetailsViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_place_details, container, false)
    }

    fun setScrollProgress(progress: Float) {
        Timber.e(progress.toString())

        headerShadow.alpha = 1 - progress
        headerShadow.isVisible = progress <= 0.9

        activeHeaderColor.alpha = 1 - (1 - progress)

        val darkColor = ContextCompat.getColor(requireContext(), R.color.text_primary)
        val lightColor = ContextCompat.getColor(requireContext(), R.color.white)
        val color = ArgbEvaluator().evaluate(1 - progress, lightColor, darkColor) as Int
        name.setTextColor(color)
    }

    fun setPlace(place: Place) {
        //checkMark.isVisible = place.openedClaims > 0 && place.closedClaims == 0
        //warning.isVisible = place.closedClaims > 0

        if (TextUtils.isEmpty(place.name)) {
            name.setText(R.string.name_unknown)
        } else {
            name.text = place.name
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

        val acceptedCurrencies = runBlocking {
            model.currenciesPlacesRepository.findByPlaceId(place.id).map {
                model.currenciesRepository.find(it.currencyId)
            }
        }
            .filterNotNull()

        val acceptedCurrenciesString = StringBuilder().apply {
            for (currency in acceptedCurrencies) {
                append(currency.name)

                if (currency != acceptedCurrencies.last()) {
                    append(", ")
                }
            }
        }

        currencies.text = acceptedCurrenciesString
    }

    private fun sharePlace(place: Place) {
        val subject = resources.getString(R.string.share_place_message_title)

        val text = resources.getString(
            R.string.share_place_message_text,
            String.format(
                "https://www.google.com/maps/@%s,%s,19z?hl=en",
                place.latitude,
                place.longitude
            )
        )

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, text)
        }

        requireContext().startActivity(Intent.createChooser(intent, "Share"))
    }
}