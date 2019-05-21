package com.bubelov.coins.picklocation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.bubelov.coins.R
import com.bubelov.coins.util.activityViewModelProvider
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_pick_location.*
import javax.inject.Inject

class PickLocationFragment : DaggerFragment() {
    @Inject lateinit var modelFactory: ViewModelProvider.Factory

    private val resultModel by lazy {
        activityViewModelProvider(modelFactory) as PickLocationResultViewModel
    }

    private val initialLocation by lazy {
        PickLocationFragmentArgs.fromBundle(arguments!!).initialLocation
    }

//    private var map: GoogleMap? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_pick_location, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        toolbar.apply {
            setNavigationOnClickListener { findNavController().popBackStack() }
            inflateMenu(R.menu.pick_location)

            setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.action_done -> {
                        val map = map ?: return@setOnMenuItemClickListener true

//                        resultModel.pickLocation(Location(
//                            map.cameraPosition.target.latitude,
//                            map.cameraPosition.target.longitude
//                        ))

                        findNavController().popBackStack()
                        true
                    }
                    else -> false
                }
            }
        }

//        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
//        mapFragment.getMapAsync(this)
    }

//    override fun onMapReady(map: GoogleMap) {
//        this.map = map
//
//        map.moveCamera(
//            CameraUpdateFactory.newLatLngZoom(
//                initialLocation.toLatLng(),
//                INITIAL_ZOOM
//            )
//        )
//    }

    companion object {
        const val INITIAL_ZOOM = 15f
    }
}