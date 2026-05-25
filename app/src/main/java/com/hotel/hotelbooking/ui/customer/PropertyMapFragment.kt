package com.hotel.hotelbooking.ui.customer

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.hotel.hotelbooking.R
import com.hotel.hotelbooking.data.util.Resource
import com.hotel.hotelbooking.databinding.FragmentPropertyMapBinding
import com.hotel.hotelbooking.ui.util.applySystemBarBottomMargin
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PropertyMapFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentPropertyMapBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PropertyMapViewModel by viewModels()
    private var googleMap: GoogleMap? = null
    private var propertyLatLng: LatLng? = null

    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) enableMyLocation()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPropertyMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }
        binding.fabDirections.applySystemBarBottomMargin(
            resources.getDimensionPixelOffset(R.dimen.spacing_md)
        )

        val mapFragment = childFragmentManager.findFragmentById(R.id.map_container) as? SupportMapFragment
            ?: SupportMapFragment.newInstance().also {
                childFragmentManager.beginTransaction()
                    .replace(R.id.map_container, it).commit()
            }
        mapFragment.getMapAsync(this)

        viewModel.property.observe(viewLifecycleOwner) { resource ->
            if (resource is Resource.Success) {
                resource.data?.let { property ->
                    binding.toolbar.title = property.name
                    val lat = property.location.latitude
                    val lng = property.location.longitude
                    if (lat != 0.0 || lng != 0.0) {
                        val latLng = LatLng(lat, lng)
                        propertyLatLng = latLng
                        googleMap?.let { map ->
                            map.clear()
                            map.addMarker(MarkerOptions().position(latLng).title(property.name))
                            map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
                        }
                    }

                    binding.fabDirections.setOnClickListener {
                        openInGoogleMaps(lat, lng, property.name)
                    }
                }
            }
        }

        requestLocationIfNeeded()
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        map.uiSettings.isZoomControlsEnabled = true
        map.uiSettings.isMyLocationButtonEnabled = true

        enableMyLocation()

        propertyLatLng?.let { latLng ->
            map.addMarker(MarkerOptions().position(latLng))
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
        }
    }

    private fun enableMyLocation() {
        val fineGranted = ContextCompat.checkSelfPermission(
            requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val coarseGranted = ContextCompat.checkSelfPermission(
            requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        if (fineGranted || coarseGranted) {
            googleMap?.isMyLocationEnabled = true
        }
    }

    private fun requestLocationIfNeeded() {
        val granted = ContextCompat.checkSelfPermission(
            requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        if (!granted) {
            locationPermissionRequest.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun openInGoogleMaps(lat: Double, lng: Double, label: String) {
        val uri = Uri.parse("geo:$lat,$lng?q=$lat,$lng(${Uri.encode(label)})")
        val intent = Intent(Intent.ACTION_VIEW, uri).apply {
            setPackage("com.google.android.apps.maps")
        }
        if (intent.resolveActivity(requireContext().packageManager) != null) {
            startActivity(intent)
        } else {
            // Fallback: open in browser
            val webUri = Uri.parse("https://www.google.com/maps/search/?api=1&query=$lat,$lng")
            startActivity(Intent(Intent.ACTION_VIEW, webUri))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
