package com.fallTurtle.myrestaurantgallery.view_model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.fallTurtle.myrestaurantgallery.repository.LocationRepository
import com.fallTurtle.myrestaurantgallery.model.retrofit.response.LocationResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response

class LocationSearchViewModel(application: Application) : AndroidViewModel(application) {
    //지역 검색 비즈니스 로직 리포지토리
    private val searchRepository = LocationRepository()

    //model 변화를 적용할 livedata. auto 변경하여 final 최종 적용
    val finalResponse: LiveData<Array<Response<LocationResponse>>> = searchRepository.getSearchResponse()

    /* 인자대로 검색하여 결과를 livedata 내에 적용하는 함수 */
    fun searchLocationWithQuery(query: String, page: Int){
        viewModelScope.launch(Dispatchers.IO) { searchRepository.searchTotalInfo(query, page) }
    }
}