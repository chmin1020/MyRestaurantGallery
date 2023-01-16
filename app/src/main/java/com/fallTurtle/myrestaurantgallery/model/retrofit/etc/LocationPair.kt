package com.fallTurtle.myrestaurantgallery.model.retrofit.etc

/**
 * 위치 값을 저장하는 데이터 클래스.
 * 각각 위도와 경도를 나타낸다.
 */
data class LocationPair (
    var latitude: Double = -1.0,
    var longitude: Double = -1.0
)