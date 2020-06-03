package com.bubelov.coins.map

import android.Manifest
import android.annotation.SuppressLint
import androidx.lifecycle.Observer
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Bundle
import android.preference.PreferenceManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import android.text.TextUtils
import android.view.*
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bubelov.coins.R
import com.bubelov.coins.auth.AuthResultViewModel
import com.bubelov.coins.data.Place
import com.bubelov.coins.model.Location
import com.bubelov.coins.placedetails.PlaceDetailsFragment
import com.bubelov.coins.repository.synclogs.LogsRepository
import com.bubelov.coins.search.PlacesSearchResultViewModel
import com.bubelov.coins.util.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_map.*
import kotlinx.android.synthetic.main.navigation_drawer_header.view.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.koin.android.viewmodel.ext.android.sharedViewModel
import org.koin.android.viewmodel.ext.android.viewModel
import org.osmdroid.events.MapListener
import org.osmdroid.events.ScrollEvent
import org.osmdroid.events.ZoomEvent
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.overlay.ItemizedIconOverlay
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

    private val authResultModel: AuthResultViewModel by sharedViewModel()

    private val placeDetailsFragment by lazy {
        childFragmentManager.findFragmentById(R.id.placeDetailsFragment) as PlaceDetailsFragment
    }

    private lateinit var drawerHeader: View

    private lateinit var drawerToggle: ActionBarDrawerToggle

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<*>

    var locationOverlay: MyLocationNewOverlay? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        org.osmdroid.config.Configuration.getInstance()
            .load(requireContext(), PreferenceManager.getDefaultSharedPreferences(requireContext()))
        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        drawerHeader = navigationView.getHeaderView(0)

        initMap()

        bottomSheetBehavior = BottomSheetBehavior.from(placeDetails).apply {
            state = BottomSheetBehavior.STATE_HIDDEN

            setBottomSheetCallback(object :
                BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {

                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                    locationFab.isVisible = slideOffset < 0.5f
                    placeDetailsFragment.setScrollProgress(slideOffset)
                }
            })

            peekHeight = resources.getDimensionPixelSize(R.dimen.map_header_height)
        }

        editPlaceFab.setOnClickListener {
            lifecycleScope.launchWhenResumed {
                model.onEditPlaceClick()
            }
        }

        toolbar.apply {
            setNavigationOnClickListener { drawerLayout.openDrawer(navigationView) }
            inflateMenu(R.menu.map)
            setOnMenuItemClickListener(this@MapFragment)
        }

        navigationView.setNavigationItemSelectedListener { item ->
            drawerLayout.closeDrawer(navigationView, false)

            when (item.itemId) {
                R.id.action_exchange_rates -> {
                    findNavController().navigate(R.id.action_mapFragment_to_exchangeRatesFragment)
                }

                R.id.action_notification_area -> {
                    findNavController().navigate(R.id.action_mapFragment_to_notificationAreaFragment)
                }

                R.id.action_chat -> openSupportChat()

                R.id.action_support_project -> {
                    findNavController().navigate(R.id.action_mapFragment_to_supportProjectFragment)
                }

                R.id.action_settings -> {
                    findNavController().navigate(R.id.action_mapFragment_to_settingsFragment)
                }
            }

            true
        }

        drawerToggle = ActionBarDrawerToggle(
            requireActivity(),
            drawerLayout,
            toolbar,
            R.string.open,
            R.string.close
        )

        drawerLayout.addDrawerListener(drawerToggle)

        lifecycleScope.launchWhenResumed {
            updateDrawerHeader()
        }

        placeDetails.setOnClickListener {
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

        locationFab.setOnClickListener {
            val location = model.location.value
            val mapController = map.controller
            mapController.setZoom(DEFAULT_MAP_ZOOM.toDouble())
            val startPoint = GeoPoint(location.latitude, location.longitude)
            mapController.setCenter(startPoint)
        }

        placesSearchResultModel.pickedPlaceId.observe(viewLifecycleOwner, Observer { id ->
            lifecycleScope.launch {
                model.selectPlace(id ?: "")
            }
        })

        authResultModel.authorized.observe(viewLifecycleOwner, Observer {
            runBlocking {
                updateDrawerHeader()
            }

            when (model.postAuthAction) {
                PostAuthAction.ADD_PLACE -> {
                    val action = MapFragmentDirections.actionMapFragmentToEditPlaceFragment(
                        null,
                        Location(map.boundingBox.centerLatitude, map.boundingBox.centerLongitude)
                    )

                    findNavController().navigate(action)
                }

                PostAuthAction.EDIT_SELECTED_PLACE -> {
                    lifecycleScope.launch {
                        val selectedPlace =
                            model.selectedPlaceFlow.toList().lastOrNull() ?: return@launch

                        val action = MapFragmentDirections.actionMapFragmentToEditPlaceFragment(
                            selectedPlace.id,
                            Location(
                                map.boundingBox.centerLatitude,
                                map.boundingBox.centerLongitude
                            )
                        )

                        findNavController().navigate(action)
                    }
                }
            }
        })
    }

    override fun onResume() {
        super.onResume()
        map.onResume()
        drawerToggle.syncState()

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
        map.onPause()
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
            R.id.action_add -> {
                lifecycleScope.launchWhenResumed {
                    model.onAddPlaceClick()
                }
            }

            R.id.action_search -> {
                lifecycleScope.launch {
                    val action = MapFragmentDirections.actionMapFragmentToPlacesSearchFragment(
                        model.location.value
                    )

                    findNavController().navigate(action)
                }
            }

            else -> return super.onOptionsItemSelected(item)
        }

        return true
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        drawerToggle.onConfigurationChanged(newConfig)
        super.onConfigurationChanged(newConfig)
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

        map.setTileSource(TileSourceFactory.MAPNIK)

        map.zoomController.setVisibility(CustomZoomButtonsController.Visibility.NEVER)
        map.setMultiTouchControls(true)

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
                        MyLocationNewOverlay(GpsMyLocationProvider(context), map).apply {
                            enableMyLocation()
                            map.overlays += this
                        }
                }

                val mapController = map.controller
                mapController.setZoom(DEFAULT_MAP_ZOOM.toDouble())
                val startPoint = GeoPoint(it.latitude, it.longitude)
                mapController.setCenter(startPoint)
            }
        }

        var showPlacesJob: Job? = null

        map.addMapListener(object : MapListener {
            override fun onScroll(event: ScrollEvent?): Boolean {
                map.overlays.clear()

                showPlacesJob?.cancel()

                showPlacesJob = lifecycleScope.launch {
                    delay(100)

                    val items = mutableListOf<OverlayItem>()

                    model.getMarkers(
                        minLat = min(map.boundingBox.latNorth, map.boundingBox.latSouth),
                        maxLat = max(map.boundingBox.latNorth, map.boundingBox.latSouth),
                        minLon = min(map.boundingBox.lonEast, map.boundingBox.lonWest),
                        maxLon = max(map.boundingBox.lonEast, map.boundingBox.lonWest)
                    ).collect {
                        log += "Loaded ${it.size} markers from cache"

                        it.forEach { place ->
                            items += OverlayItem(
                                "Title",
                                "Description",
                                GeoPoint(place.latitude, place.longitude)
                            ).apply {
                                //setMarker(place.icon.toDrawable(resources))
                            }
                        }

                        val overlay = ItemizedIconOverlay(requireContext(), items, null)

                        if (isActive) {
                            map.overlays.add(overlay)
                            map.invalidate()
                            log += "Added ${items.size} markers"
                        }
                    }
                }

                return false
            }

            override fun onZoom(event: ZoomEvent?) = false
        })

    }

    private suspend fun updateDrawerHeader() {
        val user = model.userRepository.getUser()

        if (user != null) {
            if (!TextUtils.isEmpty(user.avatarUrl)) {
                Picasso.get()
                    .load(user.avatarUrl)
                    .transform(CircleTransformation())
                    .into(drawerHeader.avatar)
            } else {
                drawerHeader.avatar.setImageResource(R.drawable.ic_no_avatar)
            }

            if (!TextUtils.isEmpty(user.firstName)) {
                drawerHeader.userName.text = String.format("%s %s", user.firstName, user.lastName)
            } else {
                drawerHeader.userName.text = user.email
            }
        } else {
            drawerHeader.avatar.setImageResource(R.drawable.ic_no_avatar)
            drawerHeader.userName.setText(R.string.guest)
        }

        drawerHeader.setOnClickListener {
            lifecycleScope.launchWhenResumed {
                drawerLayout.closeDrawer(navigationView)
                model.onDrawerHeaderClick()
            }
        }
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

    private operator fun LogsRepository.plusAssign(message: String) {
        log.appendBlocking("MapFragment", message)
    }

    companion object {
        private const val REQUEST_ACCESS_LOCATION = 10

        private const val DEFAULT_MAP_ZOOM = 15f

        private const val PLACE_ID_ARG = "place_id"

        fun newOpenPlaceArguments(place: Place): Bundle {
            return bundleOf(Pair(PLACE_ID_ARG, place.id))
        }
    }
}