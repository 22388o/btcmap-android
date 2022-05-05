package com.bubelov.coins.picklocation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.bubelov.coins.R
import com.bubelov.coins.databinding.FragmentPickLocationBinding
import com.bubelov.coins.model.Location
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class PickLocationFragment : Fragment() {

    private val resultModel: PickLocationResultViewModel by sharedViewModel()

    private val initialLocation by lazy {
        Location(
            latitude = PickLocationFragmentArgs.fromBundle(requireArguments()).lat.toDouble(),
            longitude = PickLocationFragmentArgs.fromBundle(requireArguments()).lon.toDouble(),
        )
    }

    private var _binding: FragmentPickLocationBinding? = null
    private val binding get() = _binding!!

//    private var map: GoogleMap? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPickLocationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.toolbar.apply {
            setNavigationOnClickListener { findNavController().popBackStack() }
            inflateMenu(R.menu.pick_location)

            setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.action_done -> {
                        val map = binding.map ?: return@setOnMenuItemClickListener true

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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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