package com.fallTurtle.myrestaurantgallery.repository

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import com.fallTurtle.myrestaurantgallery.model.room.Info
import com.fallTurtle.myrestaurantgallery.model.room.InfoRoomDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.FileInputStream

class DataRepository(application: Application) {
    //room DB 관련 인스턴스 (DB, DAO, elements)
    private val database: InfoRoomDatabase = InfoRoomDatabase.getInstance(application)!!
    private val roomDao = database.infoRoomDao()
    private val items: LiveData<List<Info>> = roomDao.getAllItems()

    //firebase data 리포지토리
    private val firebaseDataRepository = FirebaseDataRepository()

    /* Firestore 데이터를 불러와서 room 데이터베이스로 전체 삽입하는 함수 */
    fun restoreFirestoreDataToRoom(){
        firebaseDataRepository.getAllFireStoreItems().get().addOnSuccessListener {
            //io 스레드 내에서 roomDB 데이터 채우기
            CoroutineScope(Dispatchers.IO).launch{ it.toObjects(Info::class.java).forEach{ item -> roomDao.insert(item) } }
        }
    }

    /* roomDB 내의 모든 데이터를 삭제하는 함수 (로그아웃, 탈퇴 시 실행) */
    fun roomClear(){
        roomDao.clearAllItems()
    }

    /* roomDB 내에서 리스트를 가져오는 작업을 정의한 함수 */
    fun roomGetList(): LiveData<List<Info>> {
        return items
    }

    /* 아이템 삽입 이벤트를 정의한 함수 */
    fun itemInsert(item: Info) {
        roomDao.insert(item)
        firebaseDataRepository.addNewItem(item)
    }

    /* 아이템 삭제 이벤트를 정의한 함수 */
    fun itemDelete(item: Info) {
        roomDao.delete(item)
        firebaseDataRepository.deleteItem(item)
    }
}