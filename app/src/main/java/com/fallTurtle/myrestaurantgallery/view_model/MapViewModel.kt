package com.fallTurtle.myrestaurantgallery.view_model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.fallTurtle.myrestaurantgallery.model.retrofit.value_object.LocationPair
import com.fallTurtle.myrestaurantgallery.repository.map.GoogleMapRepository
import com.fallTurtle.myrestaurantgallery.repository.map.MapRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MapViewModel(application: Application): AndroidViewModel(application) {
    //지역 검색 비즈니스 로직 리포지토리
    private val mapRepository:MapRepository = GoogleMapRepository(application)


    //----------------------------------------------------
    // 라이브 데이터 프로퍼티 영역

    //현재 위치(좌표)
    private val insideLocation = MutableLiveData<LocationPair?>()
    val location:LiveData<LocationPair?> = insideLocation

    //현재 주소
    private val insideAddress = MutableLiveData<String>()
    val address:LiveData<String> = insideAddress


    //----------------------------------------------------
    // 함수 영역 (지도 작업)

    /* 현재 위치의 좌표 값을 요청하는 함수 */
    fun requestCurrentLocation(){
        viewModelScope.launch(Dispatchers.IO) {
            insideLocation.postValue(mapRepository.requestCurrentLocation())
        }
    }

    /* 현재 위치의 주소 값을 요청하는 함수 */
    fun requestCurrentAddress(){
        viewModelScope.launch {
            insideLocation.value?.let {
                insideAddress.postValue(mapRepository.requestCurrentAddress(it))
            }
        }
    }

    /* 유저로부터 받은 위치 값으로 위치 갱산을 요청하는 함수 */
    fun updateLocationFromUser(latitude: Double, longitude: Double){
        if(latitude == -1.0 || longitude == -1.0)
            insideLocation.postValue(null)
        else
            insideLocation.postValue(LocationPair(latitude, longitude))
    }

}