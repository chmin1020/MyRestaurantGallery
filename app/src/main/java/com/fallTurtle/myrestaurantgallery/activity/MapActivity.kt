package com.fallTurtle.myrestaurantgallery.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.fallTurtle.myrestaurantgallery.R
import com.fallTurtle.myrestaurantgallery.databinding.ActivityMapBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng

class MapActivity : AppCompatActivity(), OnMapReadyCallback {
    //map object
    private lateinit var mMap: GoogleMap

    private lateinit var binding:ActivityMapBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //get map object here
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        //searchbar
        binding.ivBack.setOnClickListener {
            finish()
        }
    }

    override fun onMapReady(p0: GoogleMap) {
        mMap = p0
        mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
        val seoul= LatLng(37.56, 126.97)
        val position = CameraPosition.Builder().target(seoul).zoom(16f).build()
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(position))
    }
}