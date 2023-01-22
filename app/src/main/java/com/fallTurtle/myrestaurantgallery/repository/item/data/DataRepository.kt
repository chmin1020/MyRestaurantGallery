package com.fallTurtle.myrestaurantgallery.repository.item.data

import android.app.Application
import com.fallTurtle.myrestaurantgallery.model.room.Info

interface DataRepository {
    suspend fun getAllData(): List<Info>
    suspend fun getProperData(id:String): Info
    suspend fun clearData()
    suspend fun insertData(data: Info)
    suspend fun deleteData(data: Info)
}