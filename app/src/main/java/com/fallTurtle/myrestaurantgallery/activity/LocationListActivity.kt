package com.fallTurtle.myrestaurantgallery.activity

import android.os.Bundle
import android.view.KeyEvent
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fallTurtle.myrestaurantgallery.view_model.LocationSearchViewModel
import com.fallTurtle.myrestaurantgallery.adapter.LocationAdapter
import com.fallTurtle.myrestaurantgallery.databinding.ActivityLocationListBinding
import com.fallTurtle.myrestaurantgallery.dialog.ProgressDialog
import com.fallTurtle.myrestaurantgallery.etc.NetworkManager
import com.fallTurtle.myrestaurantgallery.model.etc.LocationResult

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
    private val searchObserver = Observer<List<LocationResult>> { taskWithResults(it) }
    private val progressObserver = Observer<Boolean> { if(it) progressDialog.show() else progressDialog.close() }

    //리사이클러뷰
    private val adapter =  LocationAdapter()

    //뷰 바인딩
    private val binding:ActivityLocationListBinding by lazy{ ActivityLocationListBinding.inflate(layoutInflater)}


    //--------------------------------------------
    // 액티비티 생명주기 영역

    /* onCreate()에서는 리스너와 리사이클러뷰를 설정하고 이외 사전 작업을 수행한다. */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initRecyclerView() //리사이클러뷰 설정
        initListeners() //리스너 지정
        initObservers() //observer 관찰 대상 설정

        showKeyboard() //최초 생성 시 키보드 자동 올림
    }


    //--------------------------------------------
    // 내부 함수 영역 (초기화)

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
                if (!recyclerView.canScrollVertically(1)) loadNext()
            }
        })
    }

    /* 화면 내 사용자 입력 관련 뷰들의 이벤트 리스너를 등록하는 함수 */
    private fun initListeners(){
        //뒤로 가기 버튼 클릭
        binding.ivBack.setOnClickListener { finish() }

        //키보드에서 엔터를 클릭
        binding.etSearch.setOnKeyListener{ _, keyCode, event ->
            if(keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN)
                return@setOnKeyListener true.also { doSearch(binding.etSearch.text.toString()) }
            return@setOnKeyListener false
        }

        //검색 버튼 클릭
        binding.ivSearch.setOnClickListener { doSearch(binding.etSearch.text.toString()) }
    }

    /* 각 옵저버와 뷰모델의 데이터를 연결하는 함수 */
    private fun initObservers(){
        locationViewModel.searchResults.observe(this, searchObserver)
        locationViewModel.progressing.observe(this, progressObserver)
    }


    //--------------------------------------------
    // 내부 함수 영역 (검색)

    /* 키워드를 가지고 실제 검색을 하는 함수 */
    private fun doSearch(keyword: String){
        adapter.searchSettingReset(keyword)
        hideKeyboard()

        //네트워크 상태에 따른 검색 작업 실시
        if(networkManager.checkNetworkState())
            locationViewModel.searchLocationWithQuery(keyword, 1) //첫 페이지부터 검색
        else
            Toast.makeText(this, "네트워크에 연결되어 있지 않습니다.", Toast.LENGTH_SHORT).show()
    }

    /* 다음 페이지 내용을 검색해서 가져오는 함수 */
    private fun loadNext() {
        if (adapter.isEnd) return
        locationViewModel.searchLocationWithQuery(adapter.currentKeyword, adapter.currentPage)
    }

    /* 받은 응답 값들(뷰모델 데이터)을 통해 적절한 작업을 수행하는 함수 */
    private fun taskWithResults(results: List<LocationResult>){
        adapter.update(results)
        adapter.readyToNextPage()
    }


    //--------------------------------------------
    // 내부 함수 영역 (키보드 조정)

    /* 키보드를 드러내기 위해 사용하는 함수 */
    private fun showKeyboard(){
        binding.etSearch.requestFocus()
        binding.etSearch.postDelayed({ inputManager.showSoftInput(binding.etSearch, InputMethodManager.SHOW_IMPLICIT) }, 100)
    }

    /* 키보드를 숨기기 위해 사용하는 함수 */
    private fun hideKeyboard(){
        inputManager.hideSoftInputFromWindow(binding.etSearch.windowToken, InputMethodManager.HIDE_IMPLICIT_ONLY)
    }

}