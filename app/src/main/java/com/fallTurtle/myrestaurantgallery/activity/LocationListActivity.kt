package com.fallTurtle.myrestaurantgallery.activity

import android.annotation.SuppressLint
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
 * 이 때, 검색 결과를 위해서 카카오 맵 API를 Retrofit2 객체를 활용해서 사용했다.
 * 검색 결과는 recyclerView 내부에 적절하게 배치한다.
 **/
class LocationListActivity : AppCompatActivity(), CoroutineScope {
    //editText 자동 키보드
    private val etSearch: EditText by lazy { binding.etSearch }
    private val imm: InputMethodManager by lazy { getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager }
    
    //기존 좌표 (backPressed 대비)
    private val oriLati: Double by lazy { intent.getDoubleExtra("latitude", 0.0) }
    private val oriLongi: Double by lazy { intent.getDoubleExtra("longitude", 0.0) }

    //코루틴
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main

    //리사이클러뷰
    private lateinit var adapter: LocationAdapter

    //view binding
    private val binding:ActivityLocationListBinding by lazy{ ActivityLocationListBinding.inflate(layoutInflater)}


    //--------------------------------------------
    // 액티비티 생명주기 및 오버라이딩 영역
    //

    /* onCreate()에서는 리스너와 리사이클러뷰를 설정하고 이외 사전 작업을 수행한다. */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

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
        val backTo = Intent(this, MapActivity::class.java).apply {
            putExtra("x", oriLati)
            putExtra("y", oriLongi)
        }
        setResult(RESULT_OK, backTo)
        finish()
    }


    //--------------------------------------------
    // 내부 함수 영역 (초기화)
    //

    private fun initRecyclerView(){
        //리사이클러뷰 설정
        adapter = LocationAdapter(this)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

        // 무한 스크롤 기능 구현
        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                recyclerView.adapter ?: return

                val lastVisibleItemPosition =
                    (recyclerView.layoutManager as LinearLayoutManager).findLastVisibleItemPosition()
                val totalItemCount = recyclerView.adapter!!.itemCount - 1

                // 페이지 끝에 도달한 경우
                if (!recyclerView.canScrollVertically(1) && lastVisibleItemPosition == totalItemCount
                    && !adapter.isEnd) {
                    loadNext()
                }
            }
        })
    }

    private fun initListeners(){
        //뒤로 가기
        binding.ivBack.setOnClickListener {
            imm.hideSoftInputFromWindow(etSearch.windowToken, 0)
            val backTo = Intent(this, MapActivity::class.java).apply {
                putExtra("x", oriLati)
                putExtra("y", oriLongi)
            }
            setResult(RESULT_OK, backTo)
            finish()
        }

        binding.etSearch.setOnKeyListener{ v, keyCode, event ->
            if(keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN){
                searchKeyword(binding.etSearch.text.toString())
                imm.hideSoftInputFromWindow(etSearch.windowToken, 0)
                return@setOnKeyListener true
            }
            return@setOnKeyListener false
        }
        binding.ivSearch.setOnClickListener {
            searchKeyword(binding.etSearch.text.toString())
            imm.hideSoftInputFromWindow(etSearch.windowToken, 0)
        }
    }


    //--------------------------------------------
    // 내부 함수 영역 (검색)
    //

    private fun loadNext() {
        if (binding.recyclerView.adapter?.itemCount == 0)
            return
        searchWithPage(adapter.currentSearchString, adapter.currentPage + 1)
    }

    private fun setData(searchInfo: LocationResponse, keywordString: String) {
        val places: List<Place> = searchInfo.documents
        // mocking data
        val dataList = places.map {
            LocationResult(
                it.address_name,
                it.place_name,
                it.category_name,
                LocationPair(it.y.toFloat(), it.x.toFloat())
            )
        }
        adapter.setList(dataList)
        adapter.isEnd = searchInfo.meta.is_end
        if(!adapter.isEnd)
            adapter.currentPage = adapter.currentPage + 1
        adapter.currentSearchString = keywordString

        if(adapter.itemCount == 0)
            Toast.makeText(this, "검색 결과가 없습니다...", Toast.LENGTH_SHORT).show()
    }

    private fun searchKeyword(keywordString: String) {
        searchWithPage(keywordString, 1)
    }

    private fun searchWithPage(keywordString: String, page: Int) {
        // 비동기 처리
        launch(coroutineContext) {
            try {
                binding.progressCircular.isVisible = true // 로딩 표시
                if (page == 1) {
                    adapter.clearList()
                }
                // IO 스레드 사용
                withContext(Dispatchers.IO) {
                    //key, 검색어(query), 페이지를 retrofit 객체로 보내 http 응답을 받음
                    val response = RetrofitUtil.apiService.getSearchLocation(Key.KAKAO_API, keywordString, page)

                    //response 상태가 success
                    if (response.isSuccessful) {
                        val body = response.body()
                        // Main (UI) 스레드 사용
                        withContext(Dispatchers.Main) {
                            body?.let { searchResponse ->
                                setData(searchResponse, keywordString)
                            }
                        }
                    }
                    else
                        Log.e("response fail", "error happened")
                }
            } catch (e: Exception) {
                e.printStackTrace()

            } finally {
               binding.progressCircular.isVisible = false // 로딩 표시 완료
            }
        }
    }
}