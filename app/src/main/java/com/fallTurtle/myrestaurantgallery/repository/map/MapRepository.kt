package com.fallTurtle.myrestaurantgallery.repository.map

import com.fallTurtle.myrestaurantgallery.model.retrofit.value_object.LocationPair

/**
 * 맵 리포지토리의 역할을 정의한 인터페이스.
 **/
interface MapRepository {
    /* 현재 위치를 가지고 오는 함수 */
    suspend fun requestCurrentLocation(): LocationPair?
}