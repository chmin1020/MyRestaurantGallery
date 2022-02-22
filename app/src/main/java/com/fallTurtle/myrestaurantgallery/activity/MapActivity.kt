package com.fallTurtle.myrestaurantgallery.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.fallTurtle.myrestaurantgallery.R
import com.fallTurtle.myrestaurantgallery.databinding.ActivityMapBinding
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng

class MapActivity : AppCompatActivity(), OnMapReadyCallback {
    //map object
    private lateinit var mMap: GoogleMap
    //location info
    private var flpc: FusedLocationProviderClient? = null
    private lateinit var lr: LocationRequest
    private lateinit var curLocation: Location

    private lateinit var binding:ActivityMapBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //get map object here
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        lr =  LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        //search
        binding.tvSearch.setOnClickListener {
            val intent = Intent(this, LocationListActivity::class.java)
            startActivity(intent)
        }

        binding.ivBack.setOnClickListener {
            finish()
        }
    }

    override fun onMapReady(p0: GoogleMap) {
        mMap = p0
        mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
        getLocation()
    }

    //시스템에서 위치 정보를 받음
    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            // 시스템에서 받은 location 정보를 맵에 적용
            curLocation = locationResult.lastLocation
            val now = LatLng(curLocation.latitude, curLocation.longitude)
            val position = CameraPosition.Builder().target(now).zoom(16f).build()
            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(position))
        }
    }

    //지도 관련 권한 확인
    private fun checkMapPermission() : Boolean {
        return (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
    }

    //현재 위치 받아오기 준비
    private fun getLocation(){
        curLocation = Location("now")
        flpc = LocationServices.getFusedLocationProviderClient(this)

        if(checkMapPermission())
            flpc!!.requestLocationUpdates(lr, locationCallback, Looper.myLooper()!!)
        else {
            Toast.makeText(this, "권한 오류입니다.", Toast.LENGTH_SHORT).show()
            finishAffinity()
        }
    }
}
