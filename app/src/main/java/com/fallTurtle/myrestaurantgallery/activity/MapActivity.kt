package com.fallTurtle.myrestaurantgallery.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
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
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import java.io.IOException


class MapActivity : AppCompatActivity(), OnMapReadyCallback {
    //map object
    private lateinit var mMap: GoogleMap
    private lateinit var markerOps: MarkerOptions
    private var marker: Marker? = null

    //location info
    private var flpc: FusedLocationProviderClient? = null
    private lateinit var lr: LocationRequest
    private lateinit var curLocation: Location
    private lateinit var geocoder:Geocoder

    //getResult
    val getAddr = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        flpc!!.removeLocationUpdates(locationCallback)
        curLocation.latitude = it.data?.getDoubleExtra("x", 0.0)!!
        curLocation.longitude = it.data?.getDoubleExtra("y", 0.0)!!
        moveCamera()
    }

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

        //map marking
        markerOps = MarkerOptions()
        geocoder = Geocoder(this)
        //search
        binding.btnSearch.setOnClickListener {
            val intent = Intent(this, LocationListActivity::class.java)
            intent.putExtra("latitude", curLocation.latitude)
            intent.putExtra("longitude", curLocation.longitude)
            getAddr.launch(intent)
        }
        binding.btnCur.setOnClickListener {
            val backTo = Intent(this, AddActivity::class.java).apply {
                putExtra("isChanged", true)
                putExtra("latitude", curLocation.latitude)
                putExtra("longitude", curLocation.longitude)
                putExtra("address", getAddress())
            }
            setResult(RESULT_OK, backTo)
            finish()
        }
        //gps on
        binding.fabMyLocation.setOnClickListener{
            if(checkMapPermission()) {
                flpc!!.requestLocationUpdates(lr, locationCallback, Looper.myLooper()!!)
                moveCamera()
                Toast.makeText(this,"?????? ????????? ???????????????.", Toast.LENGTH_SHORT).show()
            }
            else
                Toast.makeText(this,"?????? ??????...", Toast.LENGTH_SHORT).show()
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

    override fun onBackPressed() {
        super.onBackPressed()
        val backTo = Intent(this, AddActivity::class.java).apply {
            putExtra("isChanged", false)
        }
        setResult(RESULT_OK, backTo)
    }

    //??????????????? ?????? ????????? ??????
    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            // ??????????????? ?????? location ????????? ?????? ??????
            curLocation = locationResult.lastLocation
            moveCamera()
        }
    }

    //?????? ?????? ?????? ??????
    private fun checkMapPermission() : Boolean {
        return (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
    }

    //?????? ?????? ???????????? ??????
    private fun getLocation(){
        flpc = LocationServices.getFusedLocationProviderClient(this)

        curLocation = Location("now")
        curLocation.latitude = intent.getDoubleExtra("latitude", -1.0)
        curLocation.longitude = intent.getDoubleExtra("longitude", -1.0)

        if(curLocation.latitude == -1.0 || curLocation.longitude == -1.0) {
            Toast.makeText(this, "?????? ????????? ?????? ?????? ????????? ???????????????.", Toast.LENGTH_SHORT).show()
            if (checkMapPermission())
                flpc!!.requestLocationUpdates(lr, locationCallback, Looper.myLooper()!!)
            else {
                Toast.makeText(this, "?????? ???????????????.", Toast.LENGTH_SHORT).show()
                finishAffinity()
            }
        }
        else{
            moveCamera()
        }
    }

    //????????? ????????? ??????
    private fun getAddress() : String{
        var list: List<Address>? = null
        var addr = ""
        try {
            list = geocoder.getFromLocation(
                curLocation.latitude,  // ??????
                curLocation.longitude,  // ??????
                10
            ) // ????????? ?????? ??????
        } catch (e: IOException) {
            e.printStackTrace()
            Log.e("test", "????????? ??????")
        }
        if (list != null) {
            if (list.isEmpty())
                addr = "?????? ?????? ??? ??????"
            else {
                addr = list[0].getAddressLine(0)
                val str = addr.split(" ")
                addr = str[1]
                for(num in 2 until str.size) {
                    addr = "$addr "
                    addr += str[num]
                }
            }
        }
        return addr
    }

    private fun moveCamera(){
        val now = LatLng(curLocation.latitude, curLocation.longitude)
        val position = CameraPosition.Builder().target(now).zoom(16f).build()
        markerOps.position(now)
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(position))
        marker?.remove()
        marker = mMap.addMarker(markerOps)
    }
}
