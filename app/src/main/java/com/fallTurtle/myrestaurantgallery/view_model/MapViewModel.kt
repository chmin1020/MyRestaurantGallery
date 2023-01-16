package com.fallTurtle.myrestaurantgallery.view_model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.fallTurtle.myrestaurantgallery.model.retrofit.etc.LocationPair
import com.fallTurtle.myrestaurantgallery.repository.GoogleMapRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MapViewModel(application: Application): AndroidViewModel(application) {
    //지역 검색 비즈니스 로직 리포지토리
    private val mapRepository = GoogleMapRepository(application)

    //현재 위치
    private val insideLocation = MutableLiveData<LocationPair?>()
    val location:LiveData<LocationPair?> = insideLocation

    //현재 주소
    private val insideAddress = MutableLiveData<String>()
    val address:LiveData<String> = insideAddress

    fun requestCurrentLocation(){
        viewModelScope.launch(Dispatchers.IO) {
            insideLocation.postValue(mapRepository.requestCurrentLocation())
        }
    }

    fun requestCurrentAddress(){
        viewModelScope.launch {
            insideLocation.value?.let {
                insideAddress.postValue(mapRepository.requestCurrentAddress(it))
            }
        }
    }

    fun updateLocationFromUser(latitude: Double, longitude: Double){
        if(latitude == -1.0 || longitude == -1.0)
            insideLocation.postValue(null)
        else
            insideLocation.postValue(LocationPair(latitude, longitude))
    }

}