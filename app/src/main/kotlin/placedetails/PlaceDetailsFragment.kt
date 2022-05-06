package placedetails

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
import com.bubelov.coins.databinding.FragmentPlaceDetailsBinding
import etc.openUrl
import db.Place

class PlaceDetailsFragment : Fragment() {

    private var _binding: FragmentPlaceDetailsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaceDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun setScrollProgress(progress: Float) {
        binding.headerShadow.alpha = 1 - progress
        binding.headerShadow.isVisible = progress <= 0.9

        binding.activeHeaderColor.alpha = 1 - (1 - progress)
    }

    fun setPlace(place: Place) {
        //checkMark.isVisible = place.openedClaims > 0 && place.closedClaims == 0
        //warning.isVisible = place.closedClaims > 0

        if (TextUtils.isEmpty(place.name)) {
            binding.name.setText(R.string.name_unknown)
        } else {
            binding.name.text = place.name
        }

        if (TextUtils.isEmpty(place.description)) {
            binding.description.isVisible = false
        } else {
            binding.description.isVisible = true
            binding.description.text = place.description
        }

        if (TextUtils.isEmpty(place.phone)) {
            binding.phone.setText(R.string.not_provided)
        } else {
            binding.phone.text = place.phone
        }

        binding.website.apply {
            if (TextUtils.isEmpty(place.website)) {
                setText(R.string.not_provided)
                setTextColor(ContextCompat.getColor(context, android.R.color.holo_red_dark))
                paintFlags = binding.website.paintFlags and Paint.UNDERLINE_TEXT_FLAG.inv()
                setOnClickListener(null)
            } else {
                text = place.website
                setTextColor(ContextCompat.getColor(context, android.R.color.holo_red_dark))
                paintFlags = binding.website.paintFlags or Paint.UNDERLINE_TEXT_FLAG
                setOnClickListener {
                    if (!context.openUrl(place.website)) {
                        val text = "Can't open url: ${place.website}"
                        Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        if (TextUtils.isEmpty(place.opening_hours)) {
            binding.openingHours.setText(R.string.not_provided)
        } else {
            binding.openingHours.text = place.opening_hours
        }
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