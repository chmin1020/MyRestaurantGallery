package com.fallTurtle.myrestaurantgallery.ui.map

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.fallTurtle.myrestaurantgallery.R
import com.fallTurtle.myrestaurantgallery.data.etc.*
import com.fallTurtle.myrestaurantgallery.databinding.ActivityMapBinding
import com.fallTurtle.myrestaurantgallery.etc.*
import com.fallTurtle.myrestaurantgallery.ui.add.AddActivity
import com.fallTurtle.myrestaurantgallery.ui.locationList.LocationListActivity
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
    //뷰 바인딩
    private val binding:ActivityMapBinding by lazy { ActivityMapBinding.inflate(layoutInflater) }

    //지도 객체
    private lateinit var googleMap: GoogleMap

    //지도에 위치를 표시하기 위한 마커 객체 및 옵션
    private var marker: Marker? = null
    private val markerOps: MarkerOptions by lazy { MarkerOptions() }


    //--------------------------------------------
    // 액티비티 결과 런처

    //지역 검색 화면에서 선택 데이터 가져와 적용
    private val getLocation = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        it.data?.let{ intent->
            binding.tvCurrentRestaurant.text = intent.getStringExtra(RESTAURANT_NAME)
            moveCamera(
                intent.getDoubleExtra(LATITUDE, UNDECIDED_LOCATION),
                intent.getDoubleExtra(LONGITUDE, UNDECIDED_LOCATION),
                true
            )
        }
    }


    //--------------------------------------------
    // 액티비티 생명주기 영역

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        //네트워크 연결 필요함
        if(!NetworkWatcher.checkNetworkState(this)){
            Toast.makeText(this, R.string.network_error, Toast.LENGTH_SHORT).show()
            finish()
        }

        //지도 생성 명령
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        //체크 용도인 경우 데이터 갱신 가능성 배제
        if(intent.getBooleanExtra(FOR_CHECK, false)){
            with(binding){
                llSearchBar.visibility = View.GONE
                ivBack.visibility = View.GONE
                btnCur.visibility = View.GONE
                btnSearch.visibility = View.GONE
            }
        }
        else
            initListeners() //뷰의 리스너 세팅
    }


    //--------------------------------------------
    // overriding 영역

    /* 지도가 준비 완료 시, 지도의 설정을 진행. */
    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        googleMap.mapType = GoogleMap.MAP_TYPE_NORMAL
        initCamera() //카메라 초기 설정
    }


    //--------------------------------------------
    // 내부 함수 영역 (초기화)

    /* 뷰들의 listener 등록  */
    private fun initListeners(){
        //search 버튼을 눌렀을 때
        binding.btnSearch.setOnClickListener {
            val intent = Intent(this, LocationListActivity::class.java)
            getLocation.launch(intent)
        }

        //current 버튼을 눌렀을 때
        binding.btnCur.setOnClickListener {
            if(marker != null) {
                val backTo = Intent(this, AddActivity::class.java).apply {
                    putExtra(IS_CHANGED, true)
                    putExtra(LATITUDE, markerOps.position.latitude)
                    putExtra(LONGITUDE, markerOps.position.longitude)

                    //이미 이름이 있는지 여부
                    if (binding.tvCurrentRestaurant.text != NO_SELECTED_LOCATION)
                        putExtra(RESTAURANT_NAME, binding.tvCurrentRestaurant.text)
                }
                setResult(RESULT_OK, backTo)
                finish()
            }
            else
                notifyNoSelectLocation(false)
        }

        //back 버튼(이미지)을 눌렀을 때
        binding.ivBack.setOnClickListener { finish() }
    }

    /* 초기 카메라 세팅 */
    private fun initCamera(){
        //받은 정보 토대로 설정
        binding.tvCurrentRestaurant.text = intent.getStringExtra(RESTAURANT_NAME) ?: NO_SELECTED_LOCATION
        val latitude = intent.getDoubleExtra(LATITUDE, UNDECIDED_LOCATION)
        val longitude = intent.getDoubleExtra(LONGITUDE, UNDECIDED_LOCATION)

        //위치 저장 내용이 있으면 거기로
        if(latitude == UNDECIDED_LOCATION || longitude == UNDECIDED_LOCATION)
            notifyNoSelectLocation(true)
        else
            moveCamera(latitude, longitude, true)
    }

    //--------------------------------------------
    // 내부 함수 영역 (옵저버 후속 작업)

    /* 맵 카메라 시점 현재 위치로 이동, 맵 마커도 옮기는 함수 */
    private fun moveCamera(latitude: Double?, longitude: Double?, mark: Boolean){
        if(latitude == null || longitude == null)
            Toast.makeText(this, R.string.error_happened, Toast.LENGTH_SHORT).show()
        else{
            val now = LatLng(latitude, longitude)
            val position = CameraPosition.Builder().target(now).zoom(16f).build()
            googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(position))

            //기존 마커가 있다면 지우고, 새 위치에 마커를 추가
            if(mark) {
                markerOps.position(now)
                marker?.remove()
                marker = googleMap.addMarker(markerOps)
            }
        }
    }

    /* 현재 설정된 위치 없음 알림 */
    private fun notifyNoSelectLocation(isInit: Boolean){
        if(isInit)
            moveCamera(DEFAULT_LATITUDE, DEFAULT_LONGITUDE, false)
        Toast.makeText(this, R.string.default_location, Toast.LENGTH_SHORT).show()
    }
}