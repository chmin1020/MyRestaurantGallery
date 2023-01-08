package com.fallTurtle.myrestaurantgallery.model.etc

/**
 * 위치 값을 저장하는 데이터 클래스.
 * 각각 위도와 경도를 나타낸다.
 */
data class LocationPair (
    val latitude: Float,
    val longitude: Float
)