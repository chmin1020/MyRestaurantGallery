package com.fallTurtle.myrestaurantgallery.repository.item.data

import android.app.Application
import com.fallTurtle.myrestaurantgallery.model.room.Info
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DataRepository(application: Application) {
    private val fireStoreRepository = FireStoreRepository()
    private val roomRepository = RoomRepository(application)

    fun getSavedData() = roomRepository.getSavedData()

    fun clearLocalData(){
        roomRepository.clearSavedData()
    }

    fun restoreLocalData(){
        fireStoreRepository.getProperCollection().get().addOnSuccessListener {
            //io 스레드 내에서 roomDB 데이터 채우기
            CoroutineScope(Dispatchers.IO).launch{
                it.toObjects(Info::class.java).forEach{ item -> roomRepository.insertData(item) }
            }
        }
    }

    suspend fun insertData(data: Info){
        fireStoreRepository.insertData(data)
        roomRepository.insertData(data)
    }

    fun deleteData(data: Info){
        fireStoreRepository.deleteData(data)
        roomRepository.deleteData(data)
    }
}