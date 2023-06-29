package com.example.iglite.map

import android.content.res.Resources
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.iglite.R
import com.example.iglite.databinding.ActivityMapsBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private val viewModel: MapsViewModel by viewModels()
    private lateinit var binding: ActivityMapsBinding
    private val boundBuilder = LatLngBounds.builder()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel.getAllStories()

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        viewModel.storyResult.observe(this) {
            val locationWithCoordinate =
                it.filter { story -> story.lat != null && story.lon != null }

            try {
                val success = mMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                        this, R.raw.custom_map_style
                    )
                )
                if (!success) {
                    Toast.makeText(this, "Gagal menerapkan style", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Resources.NotFoundException) {
                Toast.makeText(this, "File tidak ditemukan", Toast.LENGTH_SHORT).show()
            }
            locationWithCoordinate.forEach { data ->
                mMap.addMarker(
                    MarkerOptions().position(LatLng(data.lat!!, data.lon!!)).title(data.name)
                )
                boundBuilder.include(LatLng(data.lat, data.lon))
            }
            val bound: LatLngBounds = boundBuilder.build()
            mMap.animateCamera(
                CameraUpdateFactory.newLatLngBounds(
                    bound,
                    resources.displayMetrics.widthPixels,
                    resources.displayMetrics.heightPixels,
                    300
                )
            )
        }
    }
}