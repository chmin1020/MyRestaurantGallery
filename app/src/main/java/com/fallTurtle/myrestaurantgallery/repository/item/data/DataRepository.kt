package com.fallTurtle.myrestaurantgallery.repository.item.data

import android.app.Application
import com.fallTurtle.myrestaurantgallery.model.room.Info
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DataRepository(application: Application) {
    private val fireStoreRepository = FireStoreRepository()
    private val roomRepository = RoomRepository(application)

    fun getSavedData() = roomRepository.getSavedData()

    suspend fun clearLocalData(){
        roomRepository.clearSavedData()
    }

    suspend fun restoreLocalData(){
        withContext(Dispatchers.IO) {
            val loadTask = fireStoreRepository.getProperCollection().get()

            //io 스레드 내에서 roomDB 데이터 채우기
            while (true) {
                if (loadTask.isComplete) {
                    loadTask.result.forEach { roomRepository.insertData(it.toObject(Info::class.java)) }
                    break
                }
            }
        }
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