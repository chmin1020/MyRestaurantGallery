package com.fallTurtle.myrestaurantgallery.repository.item.data

import com.fallTurtle.myrestaurantgallery.model.room.RestaurantInfo

/**
 * 데이터 리포지토리의 역할을 정의한 인터페이스.
 **/
interface DataRepository {
    /* 모든 데이터를 가져오는 함수 */
    suspend fun getAllData(): List<RestaurantInfo>

    /* 특정 데이터를 가져오는 함수 */
    suspend fun getProperData(id:String): RestaurantInfo?

    /* 데이터를 모두 지우는 함수 */
    suspend fun clearData()

    /* 특정 데이터를 추가하는 함수 */
    suspend fun insertData(data: RestaurantInfo)

    /* 특정 데이터를 갱신하는 함수 */
    suspend fun updateData(data: RestaurantInfo)

    /* 특정 데이터를 제거하는 함수 */
    suspend fun deleteData(data: RestaurantInfo)
}