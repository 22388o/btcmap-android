package search

import android.graphics.Bitmap

data class PlacesSearchRow(
    val placeId: String,
    val icon: Bitmap,
    val name: String,
    val distance: String
)