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

package com.bubelov.coins.editplace

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.bubelov.coins.BuildConfig
import com.bubelov.coins.R
import com.bubelov.coins.model.Location
import com.bubelov.coins.model.Place
import com.bubelov.coins.picklocation.PickLocationResultViewModel
import com.bubelov.coins.util.activityViewModelProvider
import com.bubelov.coins.util.extention.getLocation
import com.bubelov.coins.util.toLatLng
import com.bubelov.coins.util.toLocation
import com.bubelov.coins.util.viewModelProvider
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_edit_place.*
import org.joda.time.DateTime
import java.lang.IllegalStateException
import java.util.*
import javax.inject.Inject

class EditPlaceFragment : DaggerFragment(), OnMapReadyCallback {
    @Inject lateinit var modelFactory: ViewModelProvider.Factory

    private val model by lazy {
        viewModelProvider(modelFactory) as EditPlaceViewModel
    }

    private val locationResultModel by lazy {
        activityViewModelProvider(modelFactory) as PickLocationResultViewModel
    }

    private val place by lazy {
        EditPlaceFragmentArgs.fromBundle(arguments!!).place
    }

    private val passedLocation by lazy {
        EditPlaceFragmentArgs.fromBundle(arguments!!).mapLocation
    }

    private var map: GoogleMap? = null

    private var placeLocationMarker: Marker? = null

    private var pickedLocation: Location? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_edit_place, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        toolbar.apply {
            setNavigationOnClickListener { findNavController().popBackStack() }
            inflateMenu(R.menu.edit_place)

            setOnMenuItemClickListener { item ->
                if (item.itemId == R.id.action_send) {
                    submit()
                }

                true
            }
        }

        val place = place

        if (place == null) {
            toolbar.setTitle(R.string.action_add_place)
            closedSwitch.isVisible = false
        } else {
            name.setText(place.name)
            phone.setText(place.phone)
            website.setText(place.website)
            description.setText(place.description)
            openingHours.setText(place.openingHours)
        }

        (childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment).getMapAsync(this)

        closedSwitch.setOnCheckedChangeListener { _, checked ->
            name.isEnabled = !checked
            editLocationButton.isVisible = !checked
            phone.isEnabled = !checked
            website.isEnabled = !checked
            description.isEnabled = !checked
            openingHours.isEnabled = !checked
        }

        editLocationButton.setOnClickListener {
            val action = EditPlaceFragmentDirections.actionEditPlaceFragmentToPickLocationFragment(
                map.getLocation()
            )

            findNavController().navigate(action)
        }

        model.showProgress.observe(viewLifecycleOwner, Observer { showProgress ->
            content.isVisible = !showProgress
            progress.isVisible = showProgress
        })

        model.changesSubmitted.observe(viewLifecycleOwner, Observer {
            findNavController().popBackStack()
        })

        model.error.observe(viewLifecycleOwner, Observer {
            AlertDialog.Builder(requireContext())
                .setMessage(it)
                .setPositiveButton(android.R.string.ok, null)
                .show()
        })

        locationResultModel.pickedLocation.observe(viewLifecycleOwner, Observer {
            it.let { location ->
                pickedLocation = location

                map?.moveCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        LatLng(location.latitude, location.longitude),
                        MAP_ZOOM
                    )
                )
            }
        })
    }

    override fun onMapReady(map: GoogleMap) {
        this.map = map

        map.uiSettings.setAllGesturesEnabled(false)

        val pickedLocation = pickedLocation

        if (pickedLocation != null) {
            setMarker(map, pickedLocation)

            map.moveCamera(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(pickedLocation.latitude, pickedLocation.longitude),
                    MAP_ZOOM
                )
            )
        } else {
            val place = place

            val initialLocation = if (place == null) {
                passedLocation
            } else {
                LatLng(place.latitude, place.longitude).toLocation()
            }

            setMarker(map, initialLocation)

            map.moveCamera(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(initialLocation.latitude, initialLocation.longitude),
                    MAP_ZOOM
                )
            )

            this.pickedLocation = initialLocation
        }
    }

    private fun setMarker(map: GoogleMap, location: Location) {
        placeLocationMarker?.remove()

        placeLocationMarker = map.addMarker(
            MarkerOptions()
                .position(location.toLatLng())
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_marker_empty))
                .anchor(BuildConfig.MAP_MARKER_ANCHOR_U, BuildConfig.MAP_MARKER_ANCHOR_V)
        )
    }

    private fun submit() {
        if (name.length() == 0) {
            AlertDialog.Builder(requireContext())
                .setMessage(R.string.name_is_not_specified)
                .setPositiveButton(android.R.string.ok, null)
                .show()

            return
        }

        model.submitChanges(place, getUpdatedPlace())
    }

    private fun getUpdatedPlace(): Place {
        val map = map ?: throw IllegalStateException("Map is not initialized")

        return Place(
            id = place?.id ?: UUID.randomUUID().toString(),
            name = name.text.toString(),
            latitude = map.cameraPosition.target.latitude,
            longitude = map.cameraPosition.target.longitude,
            phone = phone.text.toString(),
            website = website.text.toString(),
            categoryId = place?.categoryId ?: "",
            description = description.text.toString(),
            openingHours = openingHours.text.toString(),
            visible = !closedSwitch.isChecked,
            createdAt = DateTime.now(),
            updatedAt = DateTime.now()
        )
    }

    companion object {
        const val MAP_ZOOM = 15f
    }
}