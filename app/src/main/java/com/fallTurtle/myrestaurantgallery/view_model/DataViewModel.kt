package com.fallTurtle.myrestaurantgallery.view_model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.fallTurtle.myrestaurantgallery.model.room.Info
import com.fallTurtle.myrestaurantgallery.repository.DataRepository
import kotlinx.coroutines.*

class DataViewModel(application: Application): AndroidViewModel(application) {
    private val dataTotalRepository = DataRepository(application)

    /* 재 로그인 시 파이어베이스로부터 데이터를 받아오는 함수 */
    fun restoreItemsFromAccount(){
        CoroutineScope(Dispatchers.IO).launch { dataTotalRepository.restoreFirestoreDataToRoom() }
    }

    /* 룸 DB 리스트 데이터에 접근할 때 사용되는 함수 */
    fun getAllItems() : LiveData<List<Info>> {
        return dataTotalRepository.roomGetList()
    }

    /* 룸 DB 리스트 내용을 모두 지울 때 사용하는 함수 */
    fun clearAllItems(){
        CoroutineScope(Dispatchers.IO).launch { dataTotalRepository.roomClear() }
    }

    /* 기본적인 룸 DB의 삽입(혹은 갱신) 이벤트를 위한 함수 */
    fun insertNewItem(item: Info) {
        CoroutineScope(Dispatchers.IO).launch { dataTotalRepository.itemInsert(item) }
    }

    /* 기본적인 룸 DB의 삭제 이벤트를 위한 함수 */
    fun deleteItem(item: Info) {
        CoroutineScope(Dispatchers.IO).launch { dataTotalRepository.itemDelete(item) }
    }
}