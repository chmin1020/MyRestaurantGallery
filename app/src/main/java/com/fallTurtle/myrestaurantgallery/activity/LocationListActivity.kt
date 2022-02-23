package com.fallTurtle.myrestaurantgallery.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fallTurtle.myrestaurantgallery.adapter.LocationAdapter
import com.fallTurtle.myrestaurantgallery.databinding.ActivityLocationListBinding
import com.fallTurtle.myrestaurantgallery.item.LocationPair
import com.fallTurtle.myrestaurantgallery.item.LocationResult
import com.fallTurtle.myrestaurantgallery.response.LocationResponse
import com.fallTurtle.myrestaurantgallery.response.Place
import com.fallTurtle.myrestaurantgallery.utility.Key
import com.fallTurtle.myrestaurantgallery.utility.RetrofitUtil
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext


class LocationListActivity : AppCompatActivity(), CoroutineScope {
    //editText 자동 키보드
    private lateinit var etSearch: EditText
    private lateinit var imm: InputMethodManager

    //코루틴
    private lateinit var job: Job
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    //리사이클러뷰
    private lateinit var adapter: LocationAdapter


    private lateinit var binding:ActivityLocationListBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLocationListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        job = Job()

        //리사이클러뷰 설정
        adapter = LocationAdapter()
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

        //키보드 배치를 위한 사전작업
        etSearch = binding.etSearch
        imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        //뒤로 가기
        binding.ivBack.setOnClickListener {
            imm.hideSoftInputFromWindow(etSearch.windowToken, 0)
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

        initData()
    }

    override fun onResume() {
        super.onResume()
        //키보드 바로 올리기(activity 화면 출력 후 설정해야 오류 없음)
        etSearch.requestFocus()
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun initData() {
        adapter.notifyDataSetChanged()
    }

    private fun setData(searchInfo: LocationResponse, keywordString: String) {
        val places: List<Place> = searchInfo.documents
        // mocking data
        val dataList = places.map {
            LocationResult(
                it.address_name,
                it.place_name,
                it.category_name,
                LocationPair(it.x.toFloat(), it.y.toFloat())
            )
        }
        adapter.setList(dataList)
        Log.d("dataCount", adapter.itemCount.toString())
        //adapter.currentPage = searchInfo.meta.pag.toInt()
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
                //binding.progressCircular.isVisible = true // 로딩 표시
                if (page == 1) {
                    adapter.clearList()
                }
                // IO 스레드 사용
                withContext(Dispatchers.IO) {
                    val response = RetrofitUtil.apiService.getSearchLocation(Key.KAKAO_API,keywordString)
                    if (response.isSuccessful) {
                        val body = response.body()
                        // Main (UI) 스레드 사용
                        withContext(Dispatchers.Main) {
                            body?.let { searchResponse ->
                                setData(searchResponse, keywordString)
                            }
                        }
                    }
                    else {
                        Log.e("response fail", "error happened")
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()

            } finally {
               // binding.progressCircular.isVisible = false // 로딩 표시 완료
            }
        }
    }
}