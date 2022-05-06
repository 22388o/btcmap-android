package map

import android.Manifest
import android.annotation.SuppressLint
import androidx.lifecycle.Observer
import android.content.pm.PackageManager
import android.os.Bundle
import android.preference.PreferenceManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import androidx.appcompat.widget.Toolbar
import android.view.*
import androidx.core.graphics.drawable.toDrawable
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bubelov.coins.R
import com.bubelov.coins.databinding.FragmentMapBinding
import placedetails.PlaceDetailsFragment
import search.PlacesSearchResultViewModel
import etc.*
import db.Place
import kotlinx.coroutines.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.events.MapListener
import org.osmdroid.events.ScrollEvent
import org.osmdroid.events.ZoomEvent
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.overlay.ItemizedIconOverlay
import org.osmdroid.views.overlay.ItemizedIconOverlay.OnItemGestureListener
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.OverlayItem
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import kotlin.math.max
import kotlin.math.min

class MapFragment :
    Fragment(),
    Toolbar.OnMenuItemClickListener {

    private val model: MapViewModel by viewModel()

    private val log by lazy { model.log }

    private val placesSearchResultModel: PlacesSearchResultViewModel by sharedViewModel()

    private val placeDetailsFragment by lazy {
        childFragmentManager.findFragmentById(R.id.placeDetailsFragment) as PlaceDetailsFragment
    }

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<*>

    var locationOverlay: MyLocationNewOverlay? = null

    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        org.osmdroid.config.Configuration.getInstance()
            .load(requireContext(), PreferenceManager.getDefaultSharedPreferences(requireContext()))
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initMap()

        bottomSheetBehavior = BottomSheetBehavior.from(binding.placeDetails).apply {
            state = BottomSheetBehavior.STATE_HIDDEN

            addBottomSheetCallback(object :
                BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {

                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                    binding.locationFab.isVisible = slideOffset < 0.5f
                    placeDetailsFragment.setScrollProgress(slideOffset)
                }
            })

            peekHeight = resources.getDimensionPixelSize(R.dimen.map_header_height)
        }

        binding.editPlaceFab.setOnClickListener {
            lifecycleScope.launchWhenResumed {
                model.onEditPlaceClick()
            }
        }

        binding.toolbar.apply {
            inflateMenu(R.menu.map)
            setOnMenuItemClickListener(this@MapFragment)
        }

        binding.placeDetails.setOnClickListener {
            if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_COLLAPSED) {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            } else {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED)
            }
        }

        lifecycleScope.launch {
            model.selectedPlaceFlow.collect {
                if (it != null) {
                    placeDetailsFragment.setPlace(it)
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                } else {
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                }
            }
        }

        binding.locationFab.setOnClickListener {
            val location = model.location.value
            val mapController = binding.map.controller
            mapController.setZoom(DEFAULT_MAP_ZOOM.toDouble())
            val startPoint = GeoPoint(location.latitude, location.longitude)
            mapController.setCenter(startPoint)
        }

        placesSearchResultModel.pickedPlaceId.observe(viewLifecycleOwner, Observer { id ->
            lifecycleScope.launch {
                model.selectPlace(id ?: "")
            }
        })
    }

    override fun onResume() {
        super.onResume()
        binding.map.onResume()

        val placeId = arguments?.getString(PLACE_ID_ARG)

        if (placeId != null) {
            lifecycleScope.launch {
                model.selectPlace(placeId)
            }

            //model.moveToLocation(Location(placeArg.latitude, placeArg.longitude))
        }
    }

    override fun onPause() {
        super.onPause()
        binding.map.onPause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_ACCESS_LOCATION -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //model.onLocationPermissionGranted()
            }
        }
    }

    override fun onMenuItemClick(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_search -> {
                lifecycleScope.launch {
                    val action = MapFragmentDirections.actionMapFragmentToPlacesSearchFragment(
                        model.location.value.latitude.toString(),
                        model.location.value.longitude.toString(),
                    )

                    findNavController().navigate(action)
                }
            }

            R.id.action_add -> {
                lifecycleScope.launchWhenResumed {
                    model.onAddPlaceClick()
                }
            }

            R.id.action_settings -> {
                findNavController().navigate(R.id.action_mapFragment_to_settingsFragment)
            }

            else -> return super.onOptionsItemSelected(item)
        }

        return true
    }

    fun showUserProfile() {
        findNavController().navigate(R.id.action_mapFragment_to_profileFragment)
    }

    private fun requestLocationPermissions() {
        requestPermissions(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ),
            REQUEST_ACCESS_LOCATION
        )
    }

    @SuppressLint("MissingPermission")
    private fun initMap() {
        log += "initMap()"

        if (view == null) {
            return
        }

        val appContext = requireContext().applicationContext

        org.osmdroid.config.Configuration.getInstance().load(
            appContext,
            PreferenceManager.getDefaultSharedPreferences(appContext)
        )

        binding.map.setTileSource(TileSourceFactory.MAPNIK)

        binding.map.zoomController.setVisibility(CustomZoomButtonsController.Visibility.NEVER)
        binding.map.setMultiTouchControls(true)

        //initClustering(map)

//        lifecycleScope.launch {
        //model.onMapReady()

//            model.selectedPlaceFlow.collect { place ->
//                if (place != null && model.navigateToNextSelectedPlace) {
//                    val mapController = map.controller
//                    mapController.setZoom(DEFAULT_MAP_ZOOM.toDouble())
//                    val startPoint = GeoPoint(place.latitude, place.longitude)
//                    mapController.setCenter(startPoint)
//
//                    model.navigateToNextSelectedPlace = false
//                }
//            }
//        }

//        model.moveMapToLocation.observe(viewLifecycleOwner, Observer {
//            it?.let { location ->
//                if (locationOverlay == null) {
//                    locationOverlay =
//                        MyLocationNewOverlay(GpsMyLocationProvider(context), map).apply {
//                            enableMyLocation()
//                            map.overlays += this
//                        }
//                }
//
//                val mapController = map.controller
//                mapController.setZoom(DEFAULT_MAP_ZOOM.toDouble())
//                val startPoint = GeoPoint(location.latitude, location.longitude)
//                mapController.setCenter(startPoint)
//            }
//        })

        lifecycleScope.launchWhenResumed {
            model.location.collect {
                if (locationOverlay == null) {
                    locationOverlay =
                        MyLocationNewOverlay(GpsMyLocationProvider(context), binding.map).apply {
                            enableMyLocation()
                            binding.map.overlays += this
                        }

                    val mapController = binding.map.controller
                    mapController.setZoom(DEFAULT_MAP_ZOOM.toDouble())
                    val startPoint = GeoPoint(it.latitude, it.longitude)
                    mapController.setCenter(startPoint)
                }
            }
        }

        var showPlacesJob: Job? = null

        binding.map.addMapListener(object : MapListener {
            override fun onScroll(event: ScrollEvent?): Boolean {
                binding.map.overlays.clear()

                binding.map.overlays.add(MapEventsOverlay(object : MapEventsReceiver {
                    override fun singleTapConfirmedHelper(p: GeoPoint?): Boolean {
                        viewLifecycleOwner.lifecycleScope.launch { model.selectPlace("") }
                        return true
                    }

                    override fun longPressHelper(p: GeoPoint?): Boolean {
                        return false
                    }
                }))

                showPlacesJob?.cancel()

                showPlacesJob = lifecycleScope.launch {
                    delay(100)

                    val items = mutableListOf<OverlayItem>()

                    model.getMarkers(
                        minLat = min(binding.map.boundingBox.latNorth, binding.map.boundingBox.latSouth),
                        maxLat = max(binding.map.boundingBox.latNorth, binding.map.boundingBox.latSouth),
                        minLon = min(binding.map.boundingBox.lonEast, binding.map.boundingBox.lonWest),
                        maxLon = max(binding.map.boundingBox.lonEast, binding.map.boundingBox.lonWest)
                    ).collect {
                        log += "Loaded ${it.size} markers from cache"

                        val itemsToPlaces = mutableMapOf<OverlayItem, PlaceMarker>()

                        it.forEach { place ->
                            val item = OverlayItem(
                                "Title",
                                "Description",
                                GeoPoint(place.latitude, place.longitude)
                            ).apply {
                                setMarker(place.icon.toDrawable(resources))
                            }

                            items += item
                            itemsToPlaces.put(item, place)
                        }

                        val overlay =
                            ItemizedIconOverlay(requireContext(), items, object : OnItemGestureListener<OverlayItem> {
                                override fun onItemSingleTapUp(index: Int, item: OverlayItem?): Boolean {
                                    viewLifecycleOwner.lifecycleScope.launch { model.selectPlace(itemsToPlaces[item]!!.placeId) }
                                    return true
                                }

                                override fun onItemLongPress(index: Int, item: OverlayItem?): Boolean {
                                    return false
                                }
                            })

                        if (isActive) {
                            binding.map.overlays.add(overlay)
                            binding.map.invalidate()
                            log += "Added ${items.size} markers"
                        }
                    }
                }

                return false
            }

            override fun onZoom(event: ZoomEvent?) = false
        })
    }

    private fun openSupportChat() {
        requireContext().openUrl("https://t.me/joinchat/AAAAAAwVT4aVBdFzcKKbsw")
    }

//    private fun initClustering(map: GoogleMap) {
//        val placesManager = ClusterManager<PlaceMarker>(requireContext(), map)
//        placesManager.setAnimation(false)
//        map.setOnMarkerClickListener(placesManager)
//
//        val renderer = PlacesRenderer(requireContext(), map, placesManager)
//        renderer.setAnimation(false)
//        placesManager.renderer = renderer
//
//        renderer.setOnClusterItemClickListener(ClusterItemClickListener())
//
//        map.setOnCameraIdleListener {
//            placesManager.onCameraIdle()
//            model.mapBounds.value = map.projection.visibleRegion.latLngBounds
//        }
//
//        map.setOnMapClickListener {
//            model.selectPlace("")
//        }
//
//        model.placeMarkers.observe(viewLifecycleOwner, Observer { markers ->
//            placesManager.clearItems()
//            placesManager.addItems(markers)
//            placesManager.cluster()
//        })
//    }

//    private inner class PlacesRenderer internal constructor(
//        context: Context,
//        map: GoogleMap,
//        clusterManager: ClusterManager<PlaceMarker>
//    ) : DefaultClusterRenderer<PlaceMarker>(context, map, clusterManager) {
//        override fun onBeforeClusterItemRendered(
//            placeMarker: PlaceMarker,
//            markerOptions: MarkerOptions
//        ) {
//            super.onBeforeClusterItemRendered(placeMarker, markerOptions)
//
//            markerOptions
//                .icon(BitmapDescriptorFactory.fromBitmap(placeMarker.icon))
//                .anchor(BuildConfig.MAP_MARKER_ANCHOR_U, BuildConfig.MAP_MARKER_ANCHOR_V)
//        }
//    }

//    private inner class ClusterItemClickListener :
//        ClusterManager.OnClusterItemClickListener<PlaceMarker> {
//        override fun onClusterItemClick(placeMarker: PlaceMarker): Boolean {
//            model.selectPlace(placeMarker.placeId)
//            return true
//        }
//    }

    companion object {
        private const val REQUEST_ACCESS_LOCATION = 10

        private const val DEFAULT_MAP_ZOOM = 15f

        private const val PLACE_ID_ARG = "place_id"

        fun newOpenPlaceArguments(place: Place): Bundle {
            return bundleOf(Pair(PLACE_ID_ARG, place.id))
        }
    }
}