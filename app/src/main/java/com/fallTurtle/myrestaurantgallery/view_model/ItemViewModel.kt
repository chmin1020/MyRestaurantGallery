package com.fallTurtle.myrestaurantgallery.view_model

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.fallTurtle.myrestaurantgallery.model.room.Info
import com.fallTurtle.myrestaurantgallery.repository.item.ItemRepository
import kotlinx.coroutines.*

class ItemViewModel(application: Application): AndroidViewModel(application) {
    //데이터 비즈니스 로직 리포지토리
    private val itemRepository = ItemRepository(application)

    //아이템 리스트 상태 live data -> 기본적으로 room 데이터 참조
    val dataItems: LiveData<List<Info>> = itemRepository.getItems()

    //아이템 처리 진행 여부를 알려주는 boolean 프로퍼티
    private val insideProgressing = MutableLiveData(false)
    val progressing: LiveData<Boolean> = insideProgressing

    //아이템 처리를 한 이후 해당 화면은 종료됨
    private val insideWorkFinishFlag = MutableLiveData(false)
    val workFinishFlag: LiveData<Boolean> = insideWorkFinishFlag

    private val insideSelectedItem = MutableLiveData<Info>()
    val selectedItem: LiveData<Info> = insideSelectedItem

    /* DB primary key(id)에 따른 적절한 아이템을 선택하는 함수 */
    fun setProperItem(id: String){
        viewModelScope.launch(Dispatchers.IO){
            insideProgressing.postValue(true)
            insideSelectedItem.postValue(itemRepository.getProperItem(id))
            insideProgressing.postValue(false)
        }
    }

    /* 재 로그인 시 파이어베이스로부터 데이터를 받아오는 함수 */
    fun restoreItemsFromAccount(){
        viewModelScope.launch(Dispatchers.IO) {
            insideProgressing.postValue(true)
            itemRepository.restorePreviousItem()
            insideProgressing.postValue(false)
            insideWorkFinishFlag.postValue(true)
        }
    }

    /* 룸 DB(로컬 데이터) 내용을 모두 지울 때 사용하는 함수 */
    fun clearAllLocalItems(){
        viewModelScope.launch(Dispatchers.IO) {
            insideProgressing.postValue(true)
            itemRepository.localItemClear()
            insideProgressing.postValue(false)
            insideWorkFinishFlag.postValue(true)
        }
    }

    /* 기본적인 아이템 삽입(혹은 갱신) 이벤트를 위한 함수 */
    fun insertItem(item: Info, imgUri: Uri?, preImgPath: String?) {
        viewModelScope.launch(Dispatchers.IO) {
            insideProgressing.postValue(true)
            itemRepository.itemInsert(item, imgUri, preImgPath)
            insideProgressing.postValue(false)
            insideWorkFinishFlag.postValue(true)
        }
    }

    /* 기본적인 아이템 삭제 이벤트를 위한 함수 */
    fun deleteItem(itemId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            insideProgressing.postValue(true)
            itemRepository.itemDelete(itemId)
            insideProgressing.postValue(false)
            insideWorkFinishFlag.postValue(true)
        }
    }
}