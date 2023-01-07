package com.fallTurtle.myrestaurantgallery.repository

import android.app.Application
import androidx.lifecycle.LiveData
import com.fallTurtle.myrestaurantgallery.model.room.Info
import com.fallTurtle.myrestaurantgallery.model.room.InfoRoomDatabase

class RoomRepository(application: Application) {
    //room DB 관련 인스턴스 (DB, DAO, elements)
    private val database: InfoRoomDatabase = InfoRoomDatabase.getInstance(application)!!
    private val roomDao = database.infoRoomDao()
    private val items: LiveData<List<Info>> = roomDao.getAllItems()

    //firebase data 리포지토리
    private val firebaseDataRepository = FirebaseDataRepository()

    fun roomClear(){
        roomDao.clearAllItems()
    }

    /* roomDB 내에서 리스트를 가져오는 작업을 정의한 함수 */
    fun roomGetList(): LiveData<List<Info>> {
        return items
    }

    /* roomDB에 대한 기본적인 삽입 이벤트를 정의한 함수 */
    fun roomInsert(item: Info) {
        roomDao.insert(item)

        //firebase 연동
        firebaseDataRepository.addNewItem(item)
    }

    /* roomDB에 대한 기본적인 삭제 이벤트를 정의한 함수 */
    fun roomDelete(item: Info) {
        roomDao.delete(item)

        //firebase 연동
        firebaseDataRepository.deleteItem(item)
    }
}