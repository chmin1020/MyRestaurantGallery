package com.fallTurtle.myrestaurantgallery.ui.record

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fallTurtle.myrestaurantgallery.data.room.RestaurantInfo
import com.fallTurtle.myrestaurantgallery.usecase.item.ItemDeleteUseCase
import com.fallTurtle.myrestaurantgallery.usecase.item.ItemEachSelectUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Created by 최제민 on 2023-03-22.
 */
@HiltViewModel
class RecordViewModel @Inject constructor(
    private val itemSelect: ItemEachSelectUseCase,
    private val itemDelete: ItemDeleteUseCase
): ViewModel() {
    //아이템 리스트 상태 live data -> 로컬 데이터 참조
    private val insideSelectItem = MutableLiveData<RestaurantInfo>()
    val selectItem: LiveData<RestaurantInfo> = insideSelectItem

    //아이템 처리 진행 여부를 알리는 boolean live data
    private val insideProgressing = MutableLiveData(false)
    val progressing: LiveData<Boolean> = insideProgressing

    //아이템 작업 완료를 알리는 플래그 boolean live data
    private val insideWorkFinishFlag = MutableLiveData(false)
    val workFinishFlag: LiveData<Boolean> = insideWorkFinishFlag


    //아이템 선택
    fun setProperItem(id: String){
        viewModelScope.launch{
            insideProgressing.postValue(true)
            withContext(Dispatchers.IO){ insideSelectItem.postValue(itemSelect(id))}
            insideProgressing.postValue(false)
        }
    }

    //아이템 삭제
    fun deleteItem(id: String) {
        viewModelScope.launch {
            insideProgressing.postValue(true)
            withContext(Dispatchers.IO){ itemDelete(id) }
            insideProgressing.postValue(false)
            insideWorkFinishFlag.postValue(true)
        }
    }
}
