package com.fallTurtle.myrestaurantgallery.view_model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.fallTurtle.myrestaurantgallery.model.room.Info
import com.fallTurtle.myrestaurantgallery.repository.RoomRepository
import kotlinx.coroutines.*

class RoomViewModel(application: Application): AndroidViewModel(application) {
    private val roomRepository = RoomRepository(application)

    /* 룸 DB 리스트 데이터에 접근할 때 사용되는 함수 */
    fun getAllItems() : LiveData<List<Info>> {
        return roomRepository.roomGetList()
    }

    /* 기본적인 룸 DB의 삽입 이벤트를 위한 함수 */
    fun insertNewItem(item: Info) {
        CoroutineScope(Dispatchers.IO).launch { roomRepository.roomInsert(item) }
    }

    /* 기본적인 룸 DB의 삭제 이벤트를 위한 함수 */
    fun deleteItem(item: Info) {
        CoroutineScope(Dispatchers.IO).launch { roomRepository.roomDelete(item) }
    }
}