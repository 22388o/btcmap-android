package repository.placeicon

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.VectorDrawable
import android.os.Build
import androidx.annotation.DrawableRes
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.applyCanvas
import androidx.core.graphics.createBitmap
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.toColorFilter
import androidx.core.graphics.toRect
import com.bubelov.coins.R

class PlaceIconsRepository(
    private val context: Context
) {

    private val markersCache = mutableMapOf<String, Bitmap>()

    private val emptyPinBitmap = BitmapFactory.decodeResource(
        context.resources,
        R.drawable.ic_map_marker_empty
    )

    private val pinCirclePaint = Paint().apply {
        color = Color.WHITE
        isAntiAlias = true
    }

    private val pinIconPaint = Paint().apply {
        colorFilter = PorterDuff.Mode.SRC_IN.toColorFilter(
            ContextCompat.getColor(context, android.R.color.holo_red_dark)
        )

        isAntiAlias = true
    }

    fun getMarker(placeCategory: String): Bitmap {
        var marker = markersCache[placeCategory]

        if (marker == null) {
            marker = createMarker(placeCategory)
            markersCache[placeCategory] = marker
        }

        return marker
    }

    fun getPlaceIcon(category: String): Bitmap {
        val iconId = getIconResId(category) ?: R.drawable.ic_place
        return ContextCompat.getDrawable(context, iconId)!!.toBitmap()
    }

    private fun getIconResId(category: String): Int? {
        return when (category.lowercase()) {
            "atm" -> R.drawable.ic_atm
            "restaurant" -> R.drawable.ic_restaurant
            "cafe" -> R.drawable.ic_cafe
            "hotel" -> R.drawable.ic_hotel
            "fast food" -> R.drawable.ic_fast_food
            "hospital" -> R.drawable.ic_hospital
            "pharmacy" -> R.drawable.ic_pharmacy
            "taxi" -> R.drawable.ic_taxi
            "gas" -> R.drawable.ic_gas_station
            else -> null
        }
    }

    private fun createMarker(placeCategory: String): Bitmap {
        val iconResId = getIconResId(placeCategory) ?: return createBitmap(
            emptyPinBitmap.width,
            emptyPinBitmap.height
        ).applyCanvas {
            drawBitmap(emptyPinBitmap, 0f, 0f, Paint())
        }

        return createMarker(iconResId)
    }

    fun createMarker(drawableId: Int): Bitmap {
        val pinBitmap = createBitmap(emptyPinBitmap.width, emptyPinBitmap.height).applyCanvas {
            drawBitmap(emptyPinBitmap, 0f, 0f, Paint())
        }

        pinBitmap.applyCanvas {
            drawCircle(
                pinBitmap.width.toFloat() / 2,
                pinBitmap.height.toFloat() * 0.43f,
                pinBitmap.width.toFloat() * 0.27f,
                pinCirclePaint
            )
        }

        val iconFrame = RectF(
            pinBitmap.width.toFloat() * 0.3f,
            pinBitmap.width.toFloat() * 0.23f,
            pinBitmap.width.toFloat() * 0.7f,
            pinBitmap.height.toFloat() * 0.63f
        ).toRect()

        val iconBitmap = toBitmap(
            drawableId,
            iconFrame.right - iconFrame.left,
            iconFrame.bottom - iconFrame.top
        )

        pinBitmap.applyCanvas {
            drawBitmap(iconBitmap, null, iconFrame, pinIconPaint)
        }

        return pinBitmap
    }

    private fun toBitmap(
        @DrawableRes drawableId: Int,
        preferredWidth: Int,
        preferredHeight: Int
    ): Bitmap {
        val drawable = ContextCompat.getDrawable(context, drawableId)

        return if (drawable is BitmapDrawable) {
            drawable.bitmap
        } else if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && drawable is VectorDrawable) || drawable is VectorDrawableCompat) {
            val bitmap = createBitmap(preferredWidth, preferredHeight)
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            bitmap
        } else {
            throw IllegalArgumentException("Unsupported drawable")
        }
    }
}