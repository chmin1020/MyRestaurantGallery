package com.fallTurtle.myrestaurantgallery.repository.item.data

import com.fallTurtle.myrestaurantgallery.model.room.Info

/**
 * 데이터 리포지토리의 역할을 정의한 인터페이스.
 **/
interface DataRepository {
    suspend fun getAllData(): List<Info>
    suspend fun getProperData(id:String): Info
    suspend fun clearData()
    suspend fun insertData(data: Info)
    suspend fun deleteData(data: Info)
}