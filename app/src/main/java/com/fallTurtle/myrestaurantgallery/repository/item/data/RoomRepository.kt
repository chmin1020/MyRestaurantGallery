package com.fallTurtle.myrestaurantgallery.repository.item.data

import android.app.Application
import androidx.lifecycle.LiveData
import com.fallTurtle.myrestaurantgallery.model.room.Info
import com.fallTurtle.myrestaurantgallery.model.room.InfoRoomDatabase

class RoomRepository(application: Application) {
    //room DB 관련 인스턴스 (DB, DAO, elements)
    private val database: InfoRoomDatabase = InfoRoomDatabase.getInstance(application)!!
    private val roomDao = database.infoRoomDao()
    private val items: LiveData<List<Info>> = roomDao.getAllItems()

    fun getSavedData() = items

    suspend fun clearSavedData(){
        roomDao.clearAllItems()
    }

    suspend fun insertData(item: Info){
        roomDao.insert(item)
    }

    suspend fun deleteData(item: Info){
        roomDao.delete(item)
    }

}