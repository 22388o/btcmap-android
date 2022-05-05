package map

import android.graphics.Bitmap

data class PlaceMarker(
    val placeId: String,
    val icon: Bitmap,
    val latitude: Double,
    val longitude: Double
)