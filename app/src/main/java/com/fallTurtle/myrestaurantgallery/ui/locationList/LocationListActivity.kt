package com.fallTurtle.myrestaurantgallery.ui.locationList

import android.os.Bundle
import android.view.KeyEvent
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fallTurtle.myrestaurantgallery.R
import com.fallTurtle.myrestaurantgallery.databinding.ActivityLocationListBinding
import com.fallTurtle.myrestaurantgallery.ui.dialog.ProgressDialog
import com.fallTurtle.myrestaurantgallery.data.etc.NetworkWatcher
import com.fallTurtle.myrestaurantgallery.data.retrofit.value_object.LocationInfo
import dagger.hilt.android.AndroidEntryPoint

/**
 * 맛집을 검색하는 창을 제공하는 액티비티.
 * 이 액티비티에서는 직접 식당 이름을 검색하고, 그에 맞는 결과를 확인하여 선택할 수 있다.
 * 이 때, 검색 결과를 위해서 카카오 맵 API 를 Retrofit2 객체를 활용해서 사용했다.
 **/
@AndroidEntryPoint
class LocationListActivity : AppCompatActivity() {
    //뷰 바인딩
    private val binding: ActivityLocationListBinding by lazy { ActivityLocationListBinding.inflate(layoutInflater) }

    //editText 키보드 관리자
    private val inputManager: InputMethodManager by lazy { getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager }

    //로딩 dialog
    private val progressDialog by lazy { ProgressDialog(this) }

    //뷰모델
    private val locationViewModel:LocationListViewModel by viewModels()

    //옵저버
    private val searchObserver = Observer<List<LocationInfo>> { taskWithResults(it) }
    private val progressObserver =
        Observer<Boolean> { if (it) progressDialog.create() else progressDialog.destroy() }

    //recyclerView 어댑터
    private val adapter = LocationAdapter()


    //--------------------------------------------
    // 액티비티 생명주기 영역

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        //초기 화면 세팅 지정
        initRecyclerView()
        initListeners()
        initObservers()

        showKeyboard() //키보드 자동 올림
    }


    //--------------------------------------------
    // 내부 함수 영역 (초기화)

    /* recyclerView 관련된 초기 설정을 하는 함수 */
    private fun initRecyclerView() {
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

        // 무한 스크롤 기능 구현
        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                recyclerView.adapter ?: return //어댑터 null -> 동작을 수행 x

                // 페이지 끝에 도달한 경우(위에서 구한 인덱스 비교)
                if (!recyclerView.canScrollVertically(1)) loadNext()
            }
        })
    }

    /* 화면 뷰들의 이벤트 listener 등록 함수 */
    private fun initListeners() {
        //뒤로 가기 버튼 클릭
        binding.ivBack.setOnClickListener { finish() }

        //키보드 엔터를 클릭
        binding.etSearch.setOnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN)
                return@setOnKeyListener true.also { doSearch(binding.etSearch.text.toString()) }
            return@setOnKeyListener false
        }

        //검색 버튼 클릭
        binding.ivSearch.setOnClickListener { doSearch(binding.etSearch.text.toString()) }
    }

    /* 각 옵저버 뷰모델 연결 함수 */
    private fun initObservers() {
        locationViewModel.searchResults.observe(this, searchObserver)
        locationViewModel.progressing.observe(this, progressObserver)
    }


    //--------------------------------------------
    // 내부 함수 영역 (검색)

    /* 키워드로 실제 검색을 하는 함수 */
    private fun doSearch(keyword: String) {
        adapter.searchSettingReset(keyword)
        hideKeyboard()

        //네트워크 상태에 따른 검색 작업
        if (NetworkWatcher.checkNetworkState(this))
            locationViewModel.searchLocationWithQuery(keyword, 1) //첫 페이지부터 검색
        else
            Toast.makeText(this, R.string.network_error, Toast.LENGTH_SHORT).show()
    }

    /* 다음 페이지 내용을 가져오는 함수 */
    private fun loadNext() {
        if (adapter.isEnd) return
        locationViewModel.searchLocationWithQuery(adapter.currentKeyword, adapter.currentPage)
    }

    /* 받은 응답 값들(뷰모델 데이터) 적용 함수 */
    private fun taskWithResults(results: List<LocationInfo>) {
        adapter.update(results)
        adapter.readyToNextPage()
    }


    //--------------------------------------------
    // 내부 함수 영역 (키보드 조정)

    /* 키보드 자동 드러내기 */
    private fun showKeyboard() {
        binding.etSearch.requestFocus()

        //명령 인식을 위한 딜레이
        binding.etSearch.postDelayed({
            inputManager.showSoftInput(
                binding.etSearch,
                InputMethodManager.SHOW_IMPLICIT
            )
        }, 100)
    }

    /* 키보드를 숨기기 함수 */
    private fun hideKeyboard() {
        inputManager.hideSoftInputFromWindow(
            binding.etSearch.windowToken,
            InputMethodManager.HIDE_IMPLICIT_ONLY
        )
    }
}