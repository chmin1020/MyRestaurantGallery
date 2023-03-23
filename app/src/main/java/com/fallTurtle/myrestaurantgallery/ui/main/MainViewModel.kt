package com.fallTurtle.myrestaurantgallery.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fallTurtle.myrestaurantgallery.data.room.RestaurantInfo
import com.fallTurtle.myrestaurantgallery.usecase.item.ItemAllSelectUseCase
import com.fallTurtle.myrestaurantgallery.usecase.user.LogoutUseCase
import com.fallTurtle.myrestaurantgallery.usecase.user.UserCheckUseCase
import com.fallTurtle.myrestaurantgallery.usecase.user.WithdrawUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Created by 최제민 on 2023-03-22.
 */
@HiltViewModel
class MainViewModel @Inject constructor(
    private val userCheck: UserCheckUseCase,
    private val loadItems: ItemAllSelectUseCase,
    private val doLogout: LogoutUseCase,
    private val doWithDraw: WithdrawUseCase
): ViewModel() {

    //아이템 리스트 상태 live data -> 로컬 데이터 참조
    private val insideDataItems = MutableLiveData<List<RestaurantInfo>>()
    val dataItems: LiveData<List<RestaurantInfo>> = insideDataItems

    //아이템 처리 진행 여부를 알리는 boolean live data
    private val insideProgressing = MutableLiveData(false)
    val progressing: LiveData<Boolean> = insideProgressing

    //현재 유저 상태
    private val insideUserExist = MutableLiveData(true)
    val userExist: LiveData<Boolean> = insideUserExist

    init {
        viewModelScope.launch {
            userCheck().collectLatest { insideUserExist.postValue(it) }
        }
    }

    //리스트 가져오기
    fun getAllItems(){
        viewModelScope.launch {
            insideProgressing.postValue(true)
            withContext(Dispatchers.IO) { insideDataItems.postValue(loadItems()) }
            insideProgressing.postValue(false)
        }
    }

    //로그아웃
    fun logoutUser(){
        viewModelScope.launch {
            insideProgressing.postValue(true)
            withContext(Dispatchers.IO) { doLogout() }
            insideProgressing.postValue(false)

        }
    }

    //탈퇴
    fun withdrawUser(){
        viewModelScope.launch(Dispatchers.IO) {
            insideProgressing.postValue(true)
            withContext(Dispatchers.IO) { doWithDraw() }
            insideProgressing.postValue(false)
        }
    }
}