package com.fallTurtle.myrestaurantgallery.repository.location

import com.fallTurtle.myrestaurantgallery.model.retrofit.value_object.LocationInfo

/**
 * 위치 리포지토리의 역할을 정의한 인터페이스.
 **/
interface LocationRepository {
    suspend fun searchTotalInfo(query: String, page: Int):List<LocationInfo>
}