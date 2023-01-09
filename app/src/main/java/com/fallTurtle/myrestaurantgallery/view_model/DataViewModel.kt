package com.fallTurtle.myrestaurantgallery.view_model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.fallTurtle.myrestaurantgallery.model.room.Info
import com.fallTurtle.myrestaurantgallery.repository.DataRepository
import kotlinx.coroutines.*
import java.io.FileInputStream
import java.io.InputStream
import java.util.stream.Stream

class DataViewModel(application: Application): AndroidViewModel(application) {
    //데이터 비즈니스 로직 리포지토리
    private val dataTotalRepository = DataRepository(application)

    //아이템 리스트 상태 live data -> 기본적으로 room 데이터 참조
    val dataItems: LiveData<List<Info>> = dataTotalRepository.roomGetList()

    /* 재 로그인 시 파이어베이스로부터 데이터를 받아오는 함수 */
    fun restoreItemsFromAccount(){
        viewModelScope.launch(Dispatchers.IO) { dataTotalRepository.restoreFirestoreDataToRoom() }
    }

    /* 룸 DB(로컬 데이터) 내용을 모두 지울 때 사용하는 함수 */
    fun clearAllItems(){
        viewModelScope.launch(Dispatchers.IO) { dataTotalRepository.roomClear() }
    }

    /* 기본적인 아이템 삽입(혹은 갱신) 이벤트를 위한 함수 */
    fun insertNewItem(item: Info) {
        viewModelScope.launch(Dispatchers.IO) { dataTotalRepository.itemInsert(item) }
    }

    /* 기본적인 아이템 삭제 이벤트를 위한 함수 */
    fun deleteItem(item: Info) {
        viewModelScope.launch(Dispatchers.IO) { dataTotalRepository.itemDelete(item) }
    }
}