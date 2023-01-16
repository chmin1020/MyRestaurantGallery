package com.fallTurtle.myrestaurantgallery.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.fallTurtle.myrestaurantgallery.R
import com.fallTurtle.myrestaurantgallery.databinding.ActivityMapBinding
import com.fallTurtle.myrestaurantgallery.etc.NetworkManager
import com.fallTurtle.myrestaurantgallery.model.retrofit.etc.LocationPair
import com.fallTurtle.myrestaurantgallery.view_model.MapViewModel
import androidx.lifecycle.Observer
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

/**
 * 구글 맵 API 기능을 통해 지도를 보여주는 액티비티.
 * 이 지도를 통해 검색으로 선택한 식당의 위치를 확인하거나, 현재 위치로 값을 지정할 수 있다.
 **/
class MapActivity : AppCompatActivity(), OnMapReadyCallback {
    //네트워크 연결 체크 매니저
    private val networkManager: NetworkManager by lazy { NetworkManager(this) }

    //뷰 바인딩
    private val binding:ActivityMapBinding by lazy { ActivityMapBinding.inflate(layoutInflater) }

    //뷰모델
    private val viewModelFactory by lazy{ ViewModelProvider.AndroidViewModelFactory(this.application) }
    private val mapViewModel by lazy { ViewModelProvider(this, viewModelFactory)[MapViewModel::class.java] }

    //옵저버
    private val locationObserver = Observer<LocationPair?>{ moveCamera(it) }
    private val addressObserver = Observer<String> { sendAddressInfoToPreviousPage(it) }

    //지도 객체
    private lateinit var googleMap: GoogleMap

    //지도에 위치를 표시하기 위한 마커 객체 및 옵션
    private var marker: Marker? = null
    private val markerOps: MarkerOptions by lazy { MarkerOptions() }


    //--------------------------------------------
    // 액티비티 결과 런처

    //지역 검색 화면에서 선택 데이터를 가져와 적용하는 런처
    private val getAddress = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        it.data?.let{ intent->
            mapViewModel.updateLocationFromUser(
                intent.getDoubleExtra("x", -1.0), intent.getDoubleExtra("y", -1.0)
            )
        }
    }


    //--------------------------------------------
    // 액티비티 생명주기 영역

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

        initListeners() //뷰의 리스너들 세팅
        setObservers() //옵저버 세팅
    }


    //--------------------------------------------
    // 오버라이딩 영역

    /* onMapReady()에서는 지도가 준비되었을 때, 그 지도의 설정을 진행한다. */
    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        googleMap.mapType = GoogleMap.MAP_TYPE_NORMAL
        initCamera()
    }


    //--------------------------------------------
    // 내부 함수 영역 (초기화)

    /* 화면 내 사용자 입력 관련 뷰들의 이벤트 리스너를 등록하는 함수 */
    private fun initListeners(){
        //search 버튼을 눌렀을 때
        binding.btnSearch.setOnClickListener {
            val intent = Intent(this, LocationListActivity::class.java)
            getAddress.launch(intent)
        }

        //current 버튼을 눌렀을 때
        binding.btnCur.setOnClickListener { mapViewModel.requestCurrentAddress() }

        //gps fab 버튼을 눌렀을 때
        binding.fabMyLocation.setOnClickListener{ mapViewModel.requestCurrentLocation() }

        //back 버튼(이미지)을 눌렀을 때
        binding.ivBack.setOnClickListener { finish() }
    }

    /* 초기 카메라를 세팅하는 함수 */
    private fun initCamera(){
        val latitude = intent.getDoubleExtra("latitude", -1.0)
        val longitude = intent.getDoubleExtra("longitude", -1.0)

        //위치 저장 내용이 있으면 거기로, 아니면 현재 위치로
        if(latitude == -1.0 || longitude == -1.0)
            mapViewModel.requestCurrentLocation()
        else
            mapViewModel.updateLocationFromUser(latitude, longitude)
    }

    /* 뷰모델 데이터와 옵저버를 연결하는 함수 */
    private fun setObservers(){
        mapViewModel.location.observe(this, locationObserver)
        mapViewModel.address.observe(this, addressObserver)
    }


    //--------------------------------------------
    // 내부 함수 영역 (옵저버 후속 작업)

    /* 맵 카메라를 현재 위치로 이동시키고 맵 마커도 옮기는 함수 */
    private fun moveCamera(location: LocationPair?){
        location?.let {
            //현재 설정 위치대로 이동
            val now = LatLng(it.latitude, it.longitude)
            val position = CameraPosition.Builder().target(now).zoom(16f).build()
            markerOps.position(now)
            googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(position))

            //기존 마커가 있다면 지우고, 새 위치에 마커를 추가한다.
            marker?.remove()
            marker = googleMap.addMarker(markerOps)

        } ?: Toast.makeText(this, "위치를 가져올 수 없습니다.", Toast.LENGTH_SHORT).show()
    }

    /* 주소를 포함한 모든 위치 정보를 이전 화면으로 보내는 함수 */
    private fun sendAddressInfoToPreviousPage(address: String){
        val backTo = Intent(this, AddActivity::class.java).apply {
            putExtra("isChanged", true)
            putExtra("latitude", mapViewModel.location.value?.latitude)
            putExtra("longitude", mapViewModel.location.value?.longitude)
            putExtra("address", address)
        }
        setResult(RESULT_OK, backTo)
        finish()
    }
}