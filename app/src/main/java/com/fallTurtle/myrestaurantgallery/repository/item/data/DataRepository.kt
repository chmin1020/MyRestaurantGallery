package com.fallTurtle.myrestaurantgallery.repository.item.data

import android.app.Application
import com.fallTurtle.myrestaurantgallery.model.room.Info

class DataRepository(application: Application) {
    private val fireStoreRepository = FireStoreRepository()
    private val roomRepository = RoomRepository(application)

    suspend fun getAllItems() = roomRepository.getAllItems()

    suspend fun getProperData(id:String) = roomRepository.getProperItem(id)

    suspend fun clearLocalData(){
        roomRepository.clearSavedData()
    }

    suspend fun restoreLocalData(){
        val loadResult = fireStoreRepository.getAllDataInStore()
        loadResult?.let { it.forEach { each -> roomRepository.insertData(each.toObject(Info::class.java)) } }
    }

    suspend fun insertData(data: Info){
        fireStoreRepository.insertData(data)
        roomRepository.insertData(data)
    }

    suspend fun deleteData(data: Info){
        fireStoreRepository.deleteData(data)
        roomRepository.deleteData(data)
    }
}