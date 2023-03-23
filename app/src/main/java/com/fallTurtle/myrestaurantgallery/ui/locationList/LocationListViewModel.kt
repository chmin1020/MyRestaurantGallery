package com.fallTurtle.myrestaurantgallery.ui.locationList

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fallTurtle.myrestaurantgallery.model.retrofit.value_object.LocationInfo
import com.fallTurtle.myrestaurantgallery.usecase.location_search.LocationSearchUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by 최제민 on 2023-03-22.
 */
@HiltViewModel
class LocationListViewModel
    @Inject constructor(private val locationSearch: LocationSearchUseCase) : ViewModel(){
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
            insideSearchResults.postValue(locationSearch(query, page))
            insideProgressing.postValue(false)
        }
    }
}