package com.fallTurtle.myrestaurantgallery.model.retrofit.value_object

import com.fallTurtle.myrestaurantgallery.etc.DEFAULT_LOCATION

/**
 * 위치 값을 저장하는 데이터 클래스.
 * 각각 위도와 경도를 나타낸다.
 */
data class LocationPair (
    var latitude: Double = DEFAULT_LOCATION,
    var longitude: Double = DEFAULT_LOCATION
)