package com.fallTurtle.myrestaurantgallery.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fallTurtle.myrestaurantgallery.adapter.LocationAdapter
import com.fallTurtle.myrestaurantgallery.databinding.ActivityLocationListBinding
import com.fallTurtle.myrestaurantgallery.etc.NetworkManager
import com.fallTurtle.myrestaurantgallery.item.LocationPair
import com.fallTurtle.myrestaurantgallery.item.LocationResult
import com.fallTurtle.myrestaurantgallery.retrofit.response.LocationResponse
import com.fallTurtle.myrestaurantgallery.retrofit.response.Place
import com.fallTurtle.myrestaurantgallery.retrofit.values.Key
import com.fallTurtle.myrestaurantgallery.retrofit.RetrofitUtil
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

/**
 * 맛집을 검색하는 창을 제공하는 액티비티.
 * 이 액티비티에서는 직접 식당 이름을 검색하고, 그에 맞는 결과를 확인하여 선택할 수 있다.
 * 이 때, 검색 결과를 위해서 카카오 맵 API 를 Retrofit2 객체를 활용해서 사용했다.
 * 검색 결과는 recyclerView 내부에 적절하게 배치한다.
 **/
class LocationListActivity : AppCompatActivity(), CoroutineScope {
    //--------------------------------------------
    // 상수 영역
    //

    //기존 좌표 (backPressed 대비)
    private val oriLati: Double by lazy { intent.getDoubleExtra("latitude", 0.0) }
    private val oriLongi: Double by lazy { intent.getDoubleExtra("longitude", 0.0) }

    //--------------------------------------------
    // 인스턴스 영역
    //

    //editText 자동 키보드
    private val etSearch: EditText by lazy { binding.etSearch }
    private val imm: InputMethodManager by lazy { getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager }

    //네트워크 연결 체크 매니저
    private val nm: NetworkManager by lazy { NetworkManager(this) }

    //코루틴
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main

    //리사이클러뷰
    private val adapter: LocationAdapter by lazy { LocationAdapter(this) }

    //view binding
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
    }

    /* onResume()에서는 검색을 위해 키보드를 바로 올리는 작업을 수행해준다. */
    override fun onResume() {
        super.onResume()

        //키보드 바로 올리기(activity 화면 출력 후 설정해야 오류 없음)
        etSearch.requestFocus()
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
    }

    /* onBackPressed()에서는 기존 좌표를 그대로 다시 Map 화면에 보내서 오류가 없도록 한다. */
    override fun onBackPressed() {
        cancelLocationUpdate()
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

                //어댑터가 null -> 동작을 수행하지 않음
                recyclerView.adapter ?: return

                // 페이지 끝에 도달한 경우(위에서 구한 인덱스도 비교)
                if (!recyclerView.canScrollVertically(1) && !adapter.isEnd)
                    loadNext()
            }
        })
    }

    /* 화면 내 사용자 입력 관련 뷰들의 이벤트 리스너를 등록하는 함수 */
    private fun initListeners(){
        //뒤로 가기 버튼 클릭
        binding.ivBack.setOnClickListener {
            cancelLocationUpdate()
        }

        //키보드에서 엔터를 클릭
        binding.etSearch.setOnKeyListener{ v, keyCode, event ->
            if(keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN){
                doSearch(binding.etSearch.text.toString())
                return@setOnKeyListener true
            }
            return@setOnKeyListener false
        }

        //검색 버튼 클릭
        binding.ivSearch.setOnClickListener {
            doSearch(binding.etSearch.text.toString())
        }
    }

    /* 키워드를 가지고 실제 검색을 하는 함수 */
    private fun doSearch(keyword: String){
        if(nm.checkNetworkState()) {
            searchKeyword(keyword) //검색
            imm.hideSoftInputFromWindow(etSearch.windowToken, 0) //키보드는 숨긴다.
        }
        else
            Toast.makeText(this, "네트워크에 연결되어 있지 않습니다.", Toast.LENGTH_SHORT).show()
    }

    /* 선택을 하지 않고 Map 화면으로 돌아갈 때, 기존 좌표 값을 그대로 유지하도록 하는 함수 */
    private fun cancelLocationUpdate(){
        imm.hideSoftInputFromWindow(etSearch.windowToken, 0) //키보드는 숨긴다.

        //인텐트 결과 값을 기존 좌표 값으로 설정한다.
        val backTo = Intent(this, MapActivity::class.java).apply {
            putExtra("x", oriLati)
            putExtra("y", oriLongi)
        }
        setResult(RESULT_OK, backTo)

        finish() //현재 화면 종료
    }


    //--------------------------------------------
    // 내부 함수 영역 (검색)
    //

    /* 다음 페이지 내용을 검색해서 가져오는 함수 */
    private fun loadNext() {
        if (binding.recyclerView.adapter?.itemCount == 0)
            return
        searchWithPage(adapter.currentSearchString, adapter.currentPage + 1)
    }

    /* 키워드에 대한 첫번째 검색을 하는 함수 (첫번째므로 페이지는 항상 1) */
    private fun searchKeyword(keywordString: String) {
        searchWithPage(keywordString, 1)
    }

    /* 키워드에 따라 검색을 하되, 페이지를 고려하는 함수 */
    private fun searchWithPage(keywordString: String, page: Int) {
        // 비동기 처리
        launch(coroutineContext) {
            try {
                binding.progressCircular.isVisible = true // 로딩 표시

                if (page == 1)
                    adapter.clearList()

                // IO 스레드 사용
                withContext(Dispatchers.IO) {
                    //key, 검색어(query), 페이지를 retrofit 객체로 보내 http 응답을 받음
                    val response = RetrofitUtil.apiService.getSearchLocation(
                        Key.KAKAO_API, keywordString, page
                    )

                    //response 상태가 success
                    if (response.isSuccessful) {
                        val body = response.body()

                        // Main (UI) 스레드 사용
                        withContext(Dispatchers.Main) {
                            body?.let { searchResponse ->
                                setData(searchResponse, keywordString)
                            }
                        }
                    } else
                        Log.e("response fail", "error happened")
                }
            }
            catch (e: Exception) {
                e.printStackTrace()
            }
            finally {
               binding.progressCircular.isVisible = false // 로딩 표시 완료
            }
        }
    }

    /* 다음 페이지 내용을 검색해서 가져오는 함수 */
    private fun setData(searchInfo: LocationResponse, keywordString: String) {
        //리사이클러뷰에 담을 리스트
        val places: List<Place> = searchInfo.documents

        // 검색 결과 데이터를 뷰 형식에 맞게 옮긴다.
        val dataList = places.map {
            LocationResult(
                it.address_name,
                it.place_name,
                it.category_name,
                LocationPair(it.y.toFloat(), it.x.toFloat())
            )
        }

        //어댑터에 가져온 리스트 추가
        adapter.setList(dataList)

        //검색 내용 끝 부분과 더불어 page 까지 적용
        adapter.isEnd = searchInfo.meta.is_end
        if(!adapter.isEnd)
            adapter.currentPage = adapter.currentPage + 1
        adapter.currentSearchString = keywordString //다음 페이지 검색 대비로 키워드도 저장

        //검색 결과가 없을 시
        if(adapter.itemCount == 0)
            Toast.makeText(this, "검색 결과가 없습니다...", Toast.LENGTH_SHORT).show()
    }
}