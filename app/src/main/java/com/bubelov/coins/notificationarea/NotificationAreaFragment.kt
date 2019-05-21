package com.bubelov.coins.notificationarea

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.navigation.fragment.findNavController
import com.bubelov.coins.R
import com.bubelov.coins.util.OnSeekBarChangeAdapter
import com.bubelov.coins.util.viewModelProvider
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_notification_area.*
import javax.inject.Inject

class NotificationAreaFragment : DaggerFragment() {
    @Inject lateinit var modelFactory: ViewModelProvider.Factory

    private val model by lazy {
        viewModelProvider(modelFactory) as NotificationAreaViewModel
    }

//    private var map: GoogleMap? = null
//
//    private var marker: Marker? = null
//    private var areaCircle: Circle? = null

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

//        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
//        mapFragment.getMapAsync(this)
    }

//    override fun onMapReady(map: GoogleMap) {
//        this.map = map
//
//        map.apply {
//            uiSettings.isMyLocationButtonEnabled = false
//            uiSettings.isZoomControlsEnabled = false
//            uiSettings.isCompassEnabled = false
//        }
//
//        map.setOnMarkerDragListener(OnMarkerDragListener())
//
//        if (ActivityCompat.checkSelfPermission(
//                requireContext(),
//                Manifest.permission.ACCESS_FINE_LOCATION
//            ) == PackageManager.PERMISSION_GRANTED
//        ) {
//            map.isMyLocationEnabled = true
//        }
//
//        showArea(model.getNotificationArea())
//    }

//    private fun showArea(area: NotificationArea) {
//        val map = map ?: return
//
//        marker?.remove()
//        areaCircle?.remove()
//
//        val markerDescriptor =
//            BitmapDescriptorFactory.fromBitmap(model.getPinIcon())
//
//        marker = map.addMarker(
//            MarkerOptions()
//                .position(LatLng(area.latitude, area.longitude))
//                .icon(markerDescriptor)
//                .anchor(BuildConfig.MAP_MARKER_ANCHOR_U, BuildConfig.MAP_MARKER_ANCHOR_V)
//                .draggable(true)
//        )
//
//        val circleOptions = CircleOptions()
//            .center(marker?.position)
//            .radius(area.radius)
//            .fillColor(ContextCompat.getColor(requireContext(), R.color.notification_area))
//            .strokeColor(ContextCompat.getColor(requireContext(), R.color.notification_area_border))
//            .strokeWidth(4f)
//
//        val circle = map.addCircle(circleOptions)
//        areaCircle = circle
//
//        updateRadiusLabel(circle)
//
//        shrink.setOnClickListener {
//            circle.radius = circle.radius * 0.8
//            updateRadiusLabel(circle)
//        }
//
//        expand.setOnClickListener {
//            circle.radius = areaCircle!!.radius * 1.2
//            updateRadiusLabel(circle)
//        }
//
//        val areaCenter = LatLng(area.latitude, area.longitude)
//
//        map.moveCamera(
//            CameraUpdateFactory.newLatLngZoom(
//                areaCenter,
//                (model.getZoomLevel(area.radius) - 1).toFloat()
//            )
//        )
//    }

//    private fun updateRadiusLabel(circle: Circle) {
//        radius.text = getString(R.string.d_km, circle.radius.toInt() / 1000)
//    }

    private fun saveArea() {
//        val circle = areaCircle ?: return
//
//        val area = NotificationArea(
//            circle.center.latitude,
//            circle.center.longitude,
//            circle.radius
//        )
//
//        model.setNotificationArea(area)
    }

//    private inner class OnMarkerDragListener : GoogleMap.OnMarkerDragListener {
//        override fun onMarkerDragStart(marker: Marker) {
//            val circle = areaCircle ?: return
//            circle.fillColor = ContextCompat.getColor(requireContext(), android.R.color.transparent)
//        }
//
//        override fun onMarkerDrag(marker: Marker) {
//            val map = map ?: return
//            val circle = areaCircle ?: return
//            circle.center = marker.position
//            map.animateCamera(CameraUpdateFactory.newLatLng(marker.position))
//        }
//
//        override fun onMarkerDragEnd(marker: Marker) {
//            val circle = areaCircle ?: return
//            circle.fillColor = ContextCompat.getColor(requireContext(), R.color.notification_area)
//            saveArea()
//        }
//    }

    private inner class SeekBarChangeListener : OnSeekBarChangeAdapter() {
        override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
//            val circle = areaCircle ?: return
//            circle.radius = progress.toDouble()
        }
    }
}