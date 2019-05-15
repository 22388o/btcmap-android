package com.bubelov.coins.map

import android.graphics.Bitmap
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem

data class PlaceMarker(
    val placeId: String,
    val icon: Bitmap,
    val latLng: LatLng
) : ClusterItem {
    override fun getPosition() = latLng

    override fun getTitle() = ""

    override fun getSnippet() = ""
}