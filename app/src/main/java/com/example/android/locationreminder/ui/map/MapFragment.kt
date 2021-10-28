package com.example.android.locationreminder.ui.map

import android.annotation.SuppressLint
import android.content.res.Resources
import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import androidx.fragment.app.activityViewModels
import com.example.android.locationreminder.R
import com.example.android.locationreminder.databinding.FragmentMapBinding
import com.example.android.locationreminder.presentation.reminder.ReminderViewModel
import com.example.android.locationreminder.utils.createTitle
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class MapFragment : Fragment(R.layout.fragment_map), OnMapReadyCallback {

    private val viewModel by activityViewModels<ReminderViewModel>()

    private lateinit var binding: FragmentMapBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var map: GoogleMap

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentMapBinding.bind(view)
        binding.reminderViewModel = viewModel
        binding.lifecycleOwner = this

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        binding.toolbarMap.setNavigationOnClickListener { viewModel.onBackArrowClick() }
        binding.toolbarMap.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.normal_map -> {
                    map.mapType = GoogleMap.MAP_TYPE_NORMAL
                }
                R.id.hybrid_map -> {
                    map.mapType = GoogleMap.MAP_TYPE_HYBRID
                }
                R.id.satellite_map -> {
                    map.mapType = GoogleMap.MAP_TYPE_SATELLITE
                }
                R.id.terrain_map -> {
                    map.mapType = GoogleMap.MAP_TYPE_TERRAIN
                }
                else -> super.onOptionsItemSelected(it)
            }
            true
        }

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        viewModel.navigateToBack.observe(viewLifecycleOwner, {
            if (it) {
                activity?.onBackPressed()
                viewModel.onBackArrowNavigated()
            }
        })

    }


    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        setMapLongClick(map)
        setMapStyle(map)
        setPoiClick(map)
        map.isMyLocationEnabled = true
        viewModel.selectedPOI.value?.let { addMarker(it.latLng.latitude, it.latLng.longitude ); return }
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            location?.let { addMarker(it.latitude, it.longitude) }
        }
    }

    private fun addMarker(latitude: Double, longitude: Double) {
        val currentPosition = LatLng(latitude, longitude)
        val zoom = 15f
        map.addMarker(
            MarkerOptions().position(currentPosition)
                .title(getString(R.string.you_are_here))
        )
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentPosition, zoom))
    }

    private fun setMapLongClick(map: GoogleMap) {
        map.setOnMapLongClickListener { latLng ->
            // A Snippet is Additional text that's displayed below the title.
            val title = createTitle(latLng.latitude, latLng.longitude)
            viewModel.selectedPOI.value = PointOfInterest(latLng, title, title)
            val snippet = String.format(
                Locale.getDefault(),
                "Lat: %1$.5f, Long: %2$.5f",
                latLng.latitude,
                latLng.longitude
            )
            map.addMarker(
                MarkerOptions()
                    .position(latLng)
                    .title(getString(R.string.dropped_pin))
                    .snippet(snippet)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
            )
        }
    }

    private fun setPoiClick(map: GoogleMap) {
        map.setOnPoiClickListener { poi ->
            viewModel.selectedPOI.value = poi
            val poiMarker = map.addMarker(
                MarkerOptions()
                    .position(poi.latLng)
                    .title(poi.name)
            )
            poiMarker?.showInfoWindow()
        }
    }

    private fun setMapStyle(map: GoogleMap) {
        try {
            // Customize the styling of the base map using a JSON object defined
            // in a raw resource file.
            val success = map.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                    requireContext(),
                    R.raw.map_style
                )
            )

            if (!success) {
                Log.e(TAG, "Style parsing failed.")
            }
        } catch (e: Resources.NotFoundException) {
            Log.e(TAG, "Can't find style. Error: ", e)
        }
    }

    companion object {
        private const val TAG = "MapFragment"
    }
}