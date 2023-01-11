package com.fallTurtle.myrestaurantgallery.activity

import android.os.Bundle
import android.view.KeyEvent
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fallTurtle.myrestaurantgallery.view_model.LocationSearchViewModel
import com.fallTurtle.myrestaurantgallery.adapter.LocationAdapter
import com.fallTurtle.myrestaurantgallery.databinding.ActivityLocationListBinding
import com.fallTurtle.myrestaurantgallery.dialog.ProgressDialog
import com.fallTurtle.myrestaurantgallery.etc.NetworkManager
import com.fallTurtle.myrestaurantgallery.model.etc.LocationPair
import com.fallTurtle.myrestaurantgallery.model.etc.LocationResult
import com.fallTurtle.myrestaurantgallery.model.retrofit.response.LocationResponse
import retrofit2.Response

/**
 * 맛집을 검색하는 창을 제공하는 액티비티.
 * 이 액티비티에서는 직접 식당 이름을 검색하고, 그에 맞는 결과를 확인하여 선택할 수 있다.
 * 이 때, 검색 결과를 위해서 카카오 맵 API 를 Retrofit2 객체를 활용해서 사용했다.
 **/
class LocationListActivity : AppCompatActivity(){
    //editText 키보드 관리자
    private val inputManager: InputMethodManager by lazy { getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager }

    //네트워크 연결 체크 관리자
    private val networkManager: NetworkManager by lazy { NetworkManager(this) }

    //로딩 다이얼로그
    private val progressDialog by lazy { ProgressDialog(this) }

    //뷰모델
    private val viewModelFactory by lazy{ ViewModelProvider.AndroidViewModelFactory(this.application) }
    private val locationViewModel by lazy{ ViewModelProvider(this, viewModelFactory)[LocationSearchViewModel::class.java] }

    //옵저버들
    private val listObserver = Observer<Array<Response<LocationResponse>>> { taskWithResponses(it) }
    private val progressObserver = Observer<Boolean> { if(it) progressDialog.show() else progressDialog.close() }

    //리사이클러뷰
    private val adapter: LocationAdapter by lazy { LocationAdapter(this) }

    //뷰 바인딩
    private val binding:ActivityLocationListBinding by lazy{ ActivityLocationListBinding.inflate(layoutInflater)}


    //--------------------------------------------
    // 액티비티 생명주기 및 오버라이딩 영역
    //

    /* onCreate()에서는 리스너와 리사이클러뷰를 설정하고 이외 사전 작업을 수행한다. */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        //리사이클러뷰 설정 및 몇몇 뷰의 리스너 지정
        initRecyclerView()
        initListeners()

        //observer 관찰 대상 설정
        initObservers()
    }

    /* onResume()에서는 검색을 위해 키보드를 바로 올리는 작업을 수행해준다. */
    override fun onResume() {
        super.onResume()

        //키보드 바로 올리기(activity 화면 출력 후 설정해야 오류 없음)
        binding.etSearch.requestFocus()
        binding.etSearch.postDelayed(
            { inputManager.showSoftInput(binding.etSearch, InputMethodManager.SHOW_IMPLICIT) }, 100)
    }


    //--------------------------------------------
    // 내부 함수 영역 (초기화)
    //

    /* 어댑터와 레이아웃 매니저 지정, 추가 리스너 구현 등 리사이클러뷰와 관련된 초기 설정을 하는 함수 */
    private fun initRecyclerView(){
        //리사이클러뷰 설정(adapter, layoutManager)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

        // 무한 스크롤 기능 구현
        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                recyclerView.adapter ?: return //어댑터가 null -> 동작을 수행하지 않음

                // 페이지 끝에 도달한 경우(위에서 구한 인덱스도 비교)
                if (!recyclerView.canScrollVertically(1) && !adapter.isEnd)
                    loadNext()
            }
        })
    }

    /* 화면 내 사용자 입력 관련 뷰들의 이벤트 리스너를 등록하는 함수 */
    private fun initListeners(){
        //뒤로 가기 버튼 클릭
        binding.ivBack.setOnClickListener { finish()}

        //키보드에서 엔터를 클릭
        binding.etSearch.setOnKeyListener{ _, keyCode, event ->
            if(keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN)
                return@setOnKeyListener true.also { doSearch(binding.etSearch.text.toString()) }
            return@setOnKeyListener false
        }

        //검색 버튼 클릭
        binding.ivSearch.setOnClickListener { doSearch(binding.etSearch.text.toString()) }
    }

    /*각 옵저버와 뷰모델의 데이터를 연결하는 함수 */
    private fun initObservers(){
        locationViewModel.finalResponse.observe(this, listObserver)
        locationViewModel.progressing.observe(this, progressObserver)
    }


    //--------------------------------------------
    // 내부 함수 영역 (검색)
    //

    /* 키워드를 가지고 실제 검색을 하는 함수 */
    private fun doSearch(keyword: String){
        adapter.currentSearchString = keyword //현재 검색 키워드 백업

        //네트워크 상태에 따른 검색 작업 실시
        if(networkManager.checkNetworkState()) {
            searchWithPage(keyword, 1) //첫 페이지부터 검색
            inputManager.hideSoftInputFromWindow(binding.etSearch.windowToken, 0) //키보드는 숨긴다.
        }
        else
            Toast.makeText(this, "네트워크에 연결되어 있지 않습니다.", Toast.LENGTH_SHORT).show()
    }

    /* 다음 페이지 내용을 검색해서 가져오는 함수 */
    private fun loadNext() {
        if (binding.recyclerView.adapter?.itemCount == 0) return
        searchWithPage(adapter.currentSearchString, adapter.currentPage + 1)
    }

    /* 키워드에 따라 검색을 하되, 페이지를 고려하는 함수 */
    private fun searchWithPage(keywordString: String, page: Int) {
        // 뷰모델을 통한 retrofit 응답 값 get
        if (page == 1) adapter.clearList() //새로운 검색 시작
        locationViewModel.searchLocationWithQuery(keywordString, page)
    }

    /* 받은 응답 값들을 통해 적절한 작업을 수행하는 함수 */
    private fun taskWithResponses(responses: Array<Response<LocationResponse>>){
        // 음식점, 카페 검색에 대한 결과들로 각각 데이터 만들기 시도
        responses.forEach { response ->
            if (response.isSuccessful) //응답을 성공적으로 받음 -> 적절한 데이터 생성
                response.body()?.let { setData(it) }
            else //응답 실패 -> 토스트 메시지로 에러 알림
                Toast.makeText(this@LocationListActivity, "결과를 불러오지 못했습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    /* 다음 페이지 내용을 검색해서 가져오는 함수 */
    private fun setData(searchInfo: LocationResponse) {
        // 검색 결과 데이터를 뷰 형식에 맞게 옮긴다.
        val dataList = searchInfo.documents.map {
            LocationResult(it.address_name, it.place_name, it.category_name, LocationPair(it.y.toDouble(), it.x.toDouble()))
        }

        //어댑터에 가져온 리스트 추가
        adapter.setList(dataList)

        //검색 내용 끝 부분과 더불어 page 까지 적용
        adapter.isEnd = searchInfo.meta.is_end
        if(!adapter.isEnd)
            adapter.currentPage = adapter.currentPage + 1

        //검색 결과가 없을 시
        if(adapter.itemCount == 0)
            Toast.makeText(this, "검색 결과가 없습니다...", Toast.LENGTH_SHORT).show()
    }
}