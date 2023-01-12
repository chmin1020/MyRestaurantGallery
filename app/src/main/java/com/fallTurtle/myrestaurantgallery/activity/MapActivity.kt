package com.fallTurtle.myrestaurantgallery.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.fallTurtle.myrestaurantgallery.R
import com.fallTurtle.myrestaurantgallery.databinding.ActivityMapBinding
import com.fallTurtle.myrestaurantgallery.etc.NetworkManager
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

/**
 * 구글 맵을 통해 지도를 보여주는 액티비티.
 * 이 지도를 통해 검색으로 선택한 식당의 위치를 확인하거나, 현재 위치로 값을 지정할 수 있다.
 * 이를 위해 구글 맵 API 사용 및 GPS 기능 사용이 있었다.
 **/
class MapActivity : AppCompatActivity(), OnMapReadyCallback {
    //--------------------------------------------
    // 인스턴스 영역
    //

    //네트워크 연결 체크 매니저
    private val networkManager: NetworkManager by lazy { NetworkManager(this) }

    //뷰 바인딩
    private val binding:ActivityMapBinding by lazy { ActivityMapBinding.inflate(layoutInflater) }

    //지도 관련 객체
    private lateinit var mMap: GoogleMap
    private val markerOps: MarkerOptions by lazy { MarkerOptions() }
    private var marker: Marker? = null

    //location info
    private val locClient: FusedLocationProviderClient by lazy { LocationServices.getFusedLocationProviderClient(this) }
    private val locationRequest: LocationRequest
            by lazy { LocationRequest.create().apply { priority = LocationRequest.PRIORITY_HIGH_ACCURACY }}
    private lateinit var curLocation: Location
    private val geocoder:Geocoder by lazy { Geocoder(this)}

    /* 시스템에서 위치 정보를 받음 */
    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            // 시스템에서 받은 location 정보를 맵에 적용
            curLocation = locationResult.lastLocation
            moveCamera()
        }
    }

    //지역 검색 화면에서 선택 데이터를 가져와 적용하는 레지스터
    private val getAddress = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        locClient.removeLocationUpdates(locationCallback)
        it.data?.let{ intent->
            curLocation.latitude = intent.getDoubleExtra("x", 0.0)
            curLocation.longitude = intent.getDoubleExtra("y", 0.0)
        }
        moveCamera()
    }

    //지도 활용 시 필요한 looper
    private val looperForUse = Looper.myLooper() ?: Looper.getMainLooper()


    //--------------------------------------------
    // 액티비티 생명주기 및 오버라이딩 영역
    //

    /* onCreate()에서는 맵 관련 프로퍼티와 뷰 리스너를 세팅한다. */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        //지도를 불러오기 위해서는 네트워크 연결 필요함
        if(!networkManager.checkNetworkState()){
            Toast.makeText(this, "네트워크를 연결해 주세요.", Toast.LENGTH_SHORT).show()
            finish()
        }

        //지도 부분 세팅
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        //뷰의 리스너들 세팅
        initListeners()
    }

    /* onMapReady()에서는 지도가 준비되었을 때, 그 지도의 설정을 진행한다. */
    override fun onMapReady(p0: GoogleMap) {
        mMap = p0
        mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
        setMapCamera()
    }


    //--------------------------------------------
    // 내부 함수 영역
    //

    /* 화면 내 사용자 입력 관련 뷰들의 이벤트 리스너를 등록하는 함수 */
    private fun initListeners(){
        //search 버튼을 눌렀을 때 (getAddress 레지스터 실행)
        binding.btnSearch.setOnClickListener {
            val intent = Intent(this, LocationListActivity::class.java)
            getAddress.launch(intent)
        }

        //current 버튼을 눌렀을 때
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

        //gps fab 버튼을 눌렀을 때
        binding.fabMyLocation.setOnClickListener{
            //맵 관련 권한을 사용할 수 있다면 gps 기능을 통해 위치 이동
            if(checkMapPermission()) {
                locClient.requestLocationUpdates(locationRequest, locationCallback, looperForUse)
                moveCamera()
                Toast.makeText(this,"현재 위치로 이동합니다.", Toast.LENGTH_SHORT).show()
            }
            else
                Toast.makeText(this,"오류 발생...", Toast.LENGTH_SHORT).show()
        }

        //back 버튼(이미지)을 눌렀을 때
        binding.ivBack.setOnClickListener {
            finish()
        }
    }

    /* 지도 관련 권한을 확인하는 함수 */
    private fun checkMapPermission() : Boolean {
        //두 개의 지도 권한이 granted 상태인지?
        return (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
    }

    /* 현재 위치를 받아올 준비를 하는 함수 */
    private fun setMapCamera(){
        //현재 위치를 받아온다.
        curLocation = Location("now")
        curLocation.latitude = intent.getDoubleExtra("latitude", -1.0)
        curLocation.longitude = intent.getDoubleExtra("longitude", -1.0)

        //받아온 위치 데이터가 없다면 현재 위치로 카메라 설정
        if(curLocation.latitude == -1.0 || curLocation.longitude == -1.0) {
            if (checkMapPermission()) {
                Toast.makeText(this, "지정 위치가 없어 현재 위치가 표시됩니다.", Toast.LENGTH_SHORT).show()
                locClient.requestLocationUpdates(locationRequest, locationCallback, looperForUse)
            }
            else {
                Toast.makeText(this, "권한 오류입니다.", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
        else //데이터가 있다면 그 위치로 카메라를 이동
            moveCamera()
    }

    /* 맵 카메라를 현재 위치로 이동시키고 맵 마커도 옮기는 함수 */
    private fun moveCamera(){
        //현재 위치대로 이동
        val now = LatLng(curLocation.latitude, curLocation.longitude)
        val position = CameraPosition.Builder().target(now).zoom(16f).build()
        markerOps.position(now)
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(position))

        //기존 마커가 있다면 지우고, 새 위치에 마커를 추가한다.
        marker?.remove()
        marker = mMap.addMarker(markerOps)
    }

    /* 좌표를 주소로 변환하는 함수 */
    private fun getAddress() : String{
        var list: List<Address>? = null
        var address = ""

        //geocoder 객체를 통해 현재 위도와 경도로 주소 받아오기 시도
        try {
            list = geocoder.getFromLocation(curLocation.latitude, curLocation.longitude, 10)
        }
        catch (e: IOException) {
            e.printStackTrace()
        }

        //받은 주소가 성공적이라면, 받은 리스트를 String 형식으로 변환
        if (list != null) {
            if (list.isEmpty())
                address = "주소 찾을 수 없음"
            else {
                address = list[0].getAddressLine(0)
                val str = address.split(" ")
                address = str[1]
                for(num in 2 until str.size) {
                    address = "$address "
                    address += str[num]
                }
            }
        }

        //변환한 주소 반환
        return address
    }
}
