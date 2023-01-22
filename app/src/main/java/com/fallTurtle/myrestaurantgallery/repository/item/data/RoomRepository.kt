package com.fallTurtle.myrestaurantgallery.repository.item.data

import android.app.Application
import com.fallTurtle.myrestaurantgallery.model.room.Info
import com.fallTurtle.myrestaurantgallery.model.room.InfoRoomDatabase

class RoomRepository(application: Application): DataRepository {
    //room DB 관련 인스턴스 (DB, DAO, elements)
    private val database: InfoRoomDatabase = InfoRoomDatabase.getInstance(application)!!
    private val roomDao = database.infoRoomDao()


    override suspend fun getAllData(): List<Info> {
        return roomDao.getAllItems()
    }

    override suspend fun getProperData(id: String): Info {
        return roomDao.getProperItem(id)
    }

    override suspend fun clearData() {
        roomDao.clearAllItems()
    }

    override suspend fun insertData(data: Info) {
        roomDao.insert(data)
    }

    override suspend fun deleteData(data: Info) {
        roomDao.delete(data)
    }
}