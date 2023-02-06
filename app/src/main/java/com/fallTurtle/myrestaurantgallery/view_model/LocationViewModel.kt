package com.fallTurtle.myrestaurantgallery.view_model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.fallTurtle.myrestaurantgallery.model.retrofit.value_object.LocationInfo
import com.fallTurtle.myrestaurantgallery.repository.location.LocationRepository
import com.fallTurtle.myrestaurantgallery.repository.location.RetrofitLocationRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LocationViewModel(application: Application) : AndroidViewModel(application) {
    //지역 검색 비즈니스 로직 리포지토리
    private val searchRepository: LocationRepository = RetrofitLocationRepository()


    //----------------------------------------------------
    // 라이브 데이터 프로퍼티 영역

    //위치 검색 결과
    private val insideSearchResults = MutableLiveData<List<LocationInfo>>()
    val searchResults: LiveData<List<LocationInfo>> = insideSearchResults

    //진행 과정 여부
    private val insideProgressing = MutableLiveData(false)
    val progressing: LiveData<Boolean> = insideProgressing


    //----------------------------------------------------
    // 함수 영역 (위치 검색 작업)

    /* 인자대로 검색하여 결과를 livedata 내에 적용하는 함수 */
    fun searchLocationWithQuery(query: String, page: Int){
        viewModelScope.launch(Dispatchers.IO) {
            insideProgressing.postValue(true)
            insideSearchResults.postValue(searchRepository.searchTotalInfo(query, page))
            insideProgressing.postValue(false)
        }
    }
}