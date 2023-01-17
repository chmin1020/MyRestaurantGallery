package com.fallTurtle.myrestaurantgallery.repository.map

import com.fallTurtle.myrestaurantgallery.model.retrofit.value_object.LocationPair

/**
 * 맵 리포지토리의 역할을 정의한 인터페이스.
 **/
interface MapRepository {
    /* 위치(좌표)를 얻는 함수 */
    suspend fun requestCurrentLocation(): LocationPair?

    /* 좌표를 통해 주소를 얻는 함수 */
    fun requestCurrentAddress(location: LocationPair): String
}