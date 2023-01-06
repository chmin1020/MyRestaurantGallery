package com.fallTurtle.myrestaurantgallery.view_model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.fallTurtle.myrestaurantgallery.repository.LocationRepository
import com.fallTurtle.myrestaurantgallery.model.retrofit.response.LocationResponse
import kotlinx.coroutines.launch
import retrofit2.Response

class LocationSearchViewModel(application: Application) : AndroidViewModel(application) {
    //검색 전용 리포지토리
    private val searchRepository = LocationRepository()

    // model 변화를 적용할 livedata. auto 변경하여 final 최종 적용
    private val autoSearchResponse = MutableLiveData<Array<Response<LocationResponse>>>()
    val finalResponse: LiveData<Array<Response<LocationResponse>>> = autoSearchResponse

    /* 인자대로 검색하여 결과를 livedata 내에 적용하는 함수 */
    fun getResponseOfLocationSearch(query: String, page: Int){
        viewModelScope.launch {
            val restaurantResponse = searchRepository.getRestaurantInfo(query, page)
            val cafeResponse = searchRepository.getCafeInfo(query, page)

            if(restaurantResponse.isSuccessful && cafeResponse.isSuccessful){
                autoSearchResponse.postValue(arrayOf(restaurantResponse, cafeResponse))
            }
        }
    }
}