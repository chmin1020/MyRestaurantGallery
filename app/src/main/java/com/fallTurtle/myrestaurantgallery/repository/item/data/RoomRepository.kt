package com.fallTurtle.myrestaurantgallery.repository.item.data

import com.fallTurtle.myrestaurantgallery.model.room.InfoRoomDao
import com.fallTurtle.myrestaurantgallery.model.room.RestaurantInfo
import javax.inject.Inject

/**
 * Room API 기능을 통해 아이템 처리를 수행하는 리포지토리.
 **/
class RoomRepository @Inject constructor(private val roomDao: InfoRoomDao): DataRepository {
    /* 모든 데이터를 가져오는 함수 */
    override suspend fun getAllData(): List<RestaurantInfo> {
        return roomDao.getAllItems()
    }

    /* 특정 데이터를 가져오는 함수 */
    override suspend fun getProperData(id: String): RestaurantInfo? {
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

    /* 특정 데이터를 갱신하는 함수 */
    override suspend fun updateData(data: RestaurantInfo) {
        roomDao.update(data)
    }

    /* 특정 데이터를 제거하는 함수 */
    override suspend fun deleteData(data: RestaurantInfo) {
        roomDao.delete(data)
    }
}