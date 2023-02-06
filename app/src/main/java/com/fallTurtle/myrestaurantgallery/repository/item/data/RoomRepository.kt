package com.fallTurtle.myrestaurantgallery.repository.item.data

import android.app.Application
import com.fallTurtle.myrestaurantgallery.model.room.RestaurantInfo
import com.fallTurtle.myrestaurantgallery.model.room.InfoRoomDatabase

/**
 * Room API 기능을 통해 아이템 처리를 수행하는 리포지토리.
 **/
class RoomRepository(application: Application): DataRepository {
    //room DB 관련 인스턴스 (DB, DAO, elements)
    private val database: InfoRoomDatabase = InfoRoomDatabase.getInstance(application)
    private val roomDao = database.infoRoomDao()


    //--------------------------------------------
    // 오버라이딩 영역

    /* 모든 데이터를 가져오는 함수 */
    override suspend fun getAllData(): List<RestaurantInfo> {
        return roomDao.getAllItems()
    }

    /* 특정 데이터를 가져오는 함수 */
    override suspend fun getProperData(id: String): RestaurantInfo {
        return roomDao.getProperItem(id)
    }

    /* 데이터를 모두 지우는 함수 */
    override suspend fun clearData() {
        roomDao.clearAllItems()
    }

    /* 특정 데이터를 추가하는 함수 */
    override suspend fun insertData(data: RestaurantInfo) {
        roomDao.insert(data)
    }

    /* 특정 데이터를 제거하는 함수 */
    override suspend fun deleteData(data: RestaurantInfo) {
        roomDao.delete(data)
    }
}