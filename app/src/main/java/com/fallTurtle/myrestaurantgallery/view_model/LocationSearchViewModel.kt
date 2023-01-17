package com.fallTurtle.myrestaurantgallery.view_model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.fallTurtle.myrestaurantgallery.model.retrofit.value_object.LocationResult
import com.fallTurtle.myrestaurantgallery.repository.LocationRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LocationSearchViewModel(application: Application) : AndroidViewModel(application) {
    //지역 검색 비즈니스 로직 리포지토리
    private val searchRepository = LocationRepository()

    //model 변화를 적용할 livedata. auto 변경하여 final 최종 적용
    private val insideSearchResults = MutableLiveData<List<LocationResult>>()
    val searchResults: LiveData<List<LocationResult>> = insideSearchResults

    //아이템 처리 진행 여부를 알려주는 boolean 프로퍼티
    private val insideProgressing = MutableLiveData(false)
    val progressing: LiveData<Boolean> = insideProgressing

    /* 인자대로 검색하여 결과를 livedata 내에 적용하는 함수 */
    fun searchLocationWithQuery(query: String, page: Int){
        viewModelScope.launch(Dispatchers.IO) {
            insideProgressing.postValue(true)
            insideSearchResults.postValue(searchRepository.searchTotalInfo(query, page))
            insideProgressing.postValue(false)
        }
    }
}