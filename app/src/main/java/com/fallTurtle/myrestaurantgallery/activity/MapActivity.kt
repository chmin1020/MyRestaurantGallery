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
        curLocation.longitude = it.data?.getFloatExtra("x", 0F)!!.toDouble()
        curLocation.latitude = it.data?.getFloatExtra("y", 0F)!!.toDouble()

        val now = LatLng(curLocation.latitude, curLocation.longitude)
        val position = CameraPosition.Builder().target(now).zoom(16f).build()
        markerOps.position(now)
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(position))
        marker?.remove()
        mMap.addMarker(markerOps)
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
            getAddr.launch(intent)
        }
        binding.btnCur.setOnClickListener {
            val backTo = Intent(this, AddActivity::class.java).apply {
                putExtra("isChanged", true)
                putExtra("address", getAddress())
            }
            setResult(RESULT_OK, backTo)
            finish()
        }

        //gps on
        binding.fabMyLocation.setOnClickListener{
            if(checkMapPermission()) {
                flpc!!.requestLocationUpdates(lr, locationCallback, Looper.myLooper()!!)
                Toast.makeText(this,"현재 위치로 이동합니다.", Toast.LENGTH_SHORT).show()
            }
            else
                Toast.makeText(this,"오류 발생...", Toast.LENGTH_SHORT).show()
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

    //시스템에서 위치 정보를 받음
    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            // 시스템에서 받은 location 정보를 맵에 적용
            curLocation = locationResult.lastLocation
            val now = LatLng(curLocation.latitude, curLocation.longitude)
            val position = CameraPosition.Builder().target(now).zoom(16f).build()
            markerOps.position(now)
            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(position))
            marker?.remove()
            marker = mMap.addMarker(markerOps)
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

    //좌표를 주소로 변환
    private fun getAddress() : String{
        var list: List<Address>? = null
        var addr = ""
        try {
            list = geocoder.getFromLocation(
                curLocation.latitude,  // 위도
                curLocation.longitude,  // 경도
                10
            ) // 얻어올 값의 개수
        } catch (e: IOException) {
            e.printStackTrace()
            Log.e("test", "입출력 오류")
        }
        if (list != null) {
            if (list.isEmpty())
                addr = "주소 찾을 수 없음"
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
}
