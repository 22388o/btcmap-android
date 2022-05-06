package notificationarea

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toDrawable
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bubelov.coins.R
import com.bubelov.coins.databinding.FragmentNotificationAreaBinding
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polygon

class NotificationAreaFragment : Fragment() {

    private val model: NotificationAreaViewModel by viewModel()

    private var _binding: FragmentNotificationAreaBinding? = null
    private val binding get() = _binding!!

    private var marker: Marker? = null
    private var areaCircle: Polygon? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotificationAreaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.toolbar.setNavigationOnClickListener {
            saveArea()
            findNavController().popBackStack()
        }

        binding.map.zoomController.setVisibility(CustomZoomButtonsController.Visibility.NEVER)
        binding.map.setMultiTouchControls(true)

//        map.setOnMarkerDragListener(OnMarkerDragListener())

//        if (ActivityCompat.checkSelfPermission(
//                requireContext(),
//                Manifest.permission.ACCESS_FINE_LOCATION
//            ) == PackageManager.PERMISSION_GRANTED
//        ) {
//            map.isMyLocationEnabled = true
//        }

        lifecycleScope.launch {
            showArea(model.getNotificationArea())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showArea(area: NotificationArea) {
        marker?.remove(binding.map)
        binding.map.overlays.remove(areaCircle)

        marker = Marker(binding.map).apply {
            position = GeoPoint(area.latitude, area.longitude)
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            icon = model.getPinIcon().toDrawable(resources)
            setInfoWindow(null)
            isDraggable = true
            binding.map.overlays += this
        }

        val circlePoints = mutableListOf<GeoPoint>()

        (0..360).forEach {
            circlePoints += GeoPoint(area.latitude, area.longitude)
                .destinationPoint(area.radius, it.toDouble())
        }

        areaCircle = Polygon(binding.map).apply {
            points = circlePoints
            fillPaint.color = ContextCompat.getColor(requireContext(), android.R.color.holo_red_light)
            outlinePaint.color =
                ContextCompat.getColor(requireContext(), android.R.color.holo_blue_dark)
            outlinePaint.strokeWidth = 4f
            binding.map.overlays.add(0, this)
        }

        updateRadiusLabel(area)

        //shrink.setOnClickListener {
        //    circle.radius = circle.radius * 0.8
        //    updateRadiusLabel(circle)
        //}

        //expand.setOnClickListener {
        //    circle.radius = areaCircle!!.radius * 1.2
        //    updateRadiusLabel(circle)
        //}

        val areaCenter = GeoPoint(area.latitude, area.longitude)

        val mapController = binding.map.controller
        mapController.setZoom(model.getZoomLevel(area.radius).toDouble())
        mapController.setCenter(areaCenter)
    }

    private fun updateRadiusLabel(area: NotificationArea) {
        binding.radius.text = getString(R.string.d_km, area.radius.toInt() / 1000)
    }

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

    abstract class OnSeekBarChangeAdapter : SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
            // Nothing to do here
        }

        override fun onStartTrackingTouch(seekBar: SeekBar) {
            // Nothing to do here
        }

        override fun onStopTrackingTouch(seekBar: SeekBar) {
            // Nothing to do here
        }
    }
}