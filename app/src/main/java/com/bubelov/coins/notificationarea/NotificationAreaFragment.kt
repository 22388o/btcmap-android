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

package com.bubelov.coins.notificationarea

import android.Manifest
import androidx.lifecycle.ViewModelProvider
import android.content.pm.PackageManager
import android.graphics.PorterDuff
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.navigation.fragment.findNavController
import com.bubelov.coins.BuildConfig
import com.bubelov.coins.R
import com.bubelov.coins.model.NotificationArea
import com.bubelov.coins.util.OnSeekBarChangeAdapter
import com.bubelov.coins.util.viewModelProvider
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_notification_area.*
import javax.inject.Inject

class NotificationAreaFragment : DaggerFragment(), OnMapReadyCallback {
    @Inject lateinit var modelFactory: ViewModelProvider.Factory

    private val model by lazy {
        viewModelProvider(modelFactory) as NotificationAreaViewModel
    }

    private var map: GoogleMap? = null

    private var marker: Marker? = null
    private var areaCircle: Circle? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_notification_area, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        toolbar.setNavigationOnClickListener {
            saveArea()
            findNavController().popBackStack()
        }

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        radiusSeekBar.apply {
            progressDrawable.setColorFilter(
                ContextCompat.getColor(requireContext(), R.color.accent),
                PorterDuff.Mode.SRC_IN
            )

            thumb.setColorFilter(
                ContextCompat.getColor(requireContext(), R.color.accent),
                PorterDuff.Mode.SRC_IN
            )
        }
    }

    override fun onMapReady(map: GoogleMap) {
        this.map = map

        map.apply {
            uiSettings.isMyLocationButtonEnabled = false
            uiSettings.isZoomControlsEnabled = false
            uiSettings.isCompassEnabled = false
        }

        map.setOnMarkerDragListener(OnMarkerDragListener())

        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            map.isMyLocationEnabled = true
        }

        showArea(model.getNotificationArea())
    }

    private fun showArea(area: NotificationArea) {
        val map = map ?: return

        marker?.remove()
        areaCircle?.remove()

        val markerDescriptor =
            BitmapDescriptorFactory.fromResource(R.drawable.ic_map_marker_location)

        marker = map.addMarker(
            MarkerOptions()
                .position(LatLng(area.latitude, area.longitude))
                .icon(markerDescriptor)
                .anchor(BuildConfig.MAP_MARKER_ANCHOR_U, BuildConfig.MAP_MARKER_ANCHOR_V)
                .draggable(true)
        )

        val circleOptions = CircleOptions()
            .center(marker?.position)
            .radius(area.radius)
            .fillColor(ContextCompat.getColor(requireContext(), R.color.notification_area))
            .strokeColor(ContextCompat.getColor(requireContext(), R.color.notification_area_border))
            .strokeWidth(4f)

        areaCircle = map.addCircle(circleOptions)

        radiusSeekBar.apply {
            max = 500000
            progress = area.radius.toInt()
            setOnSeekBarChangeListener(SeekBarChangeListener())
        }

        val areaCenter = LatLng(area.latitude, area.longitude)

        map.moveCamera(
            CameraUpdateFactory.newLatLngZoom(
                areaCenter,
                (model.getZoomLevel(area.radius) - 1).toFloat()
            )
        )
    }

    private fun saveArea() {
        val circle = areaCircle ?: return

        val area = NotificationArea(
            circle.center.latitude,
            circle.center.longitude,
            circle.radius
        )

        model.setNotificationArea(area)
    }

    private inner class OnMarkerDragListener : GoogleMap.OnMarkerDragListener {
        override fun onMarkerDragStart(marker: Marker) {
            val circle = areaCircle ?: return
            circle.fillColor = ContextCompat.getColor(requireContext(), android.R.color.transparent)
        }

        override fun onMarkerDrag(marker: Marker) {
            val map = map ?: return
            val circle = areaCircle ?: return
            circle.center = marker.position
            map.animateCamera(CameraUpdateFactory.newLatLng(marker.position))
        }

        override fun onMarkerDragEnd(marker: Marker) {
            val circle = areaCircle ?: return
            circle.fillColor = ContextCompat.getColor(requireContext(), R.color.notification_area)
            saveArea()
        }
    }

    private inner class SeekBarChangeListener : OnSeekBarChangeAdapter() {
        override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
            val circle = areaCircle ?: return
            circle.radius = progress.toDouble()
        }
    }
}