package com.fallTurtle.myrestaurantgallery.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.fallTurtle.myrestaurantgallery.model.room.RestaurantInfo

/**
 * Created by 최제민 on 2023-03-22.
 */
class MainViewModel {
    //----------------------------------------------------
    // 라이브 데이터 프로퍼티 영역

    //아이템 리스트 상태 live data -> 로컬 데이터 참조
    private val insideDataItems = MutableLiveData<List<RestaurantInfo>>()
    val dataItems: LiveData<List<RestaurantInfo>> = insideDataItems

    //아이템 처리 진행 여부를 알려주는 boolean live data
    private val insideProgressing = MutableLiveData(false)
    val progressing: LiveData<Boolean> = insideProgressing

    //아이템 작업 완료를 알리는 플래그 boolean live data
    private val insideWorkFinishFlag = MutableLiveData(false)
    val workFinishFlag: LiveData<Boolean> = insideWorkFinishFlag

    //하나의 아이템 정보를 필요로 할 때, 그것을 가지고 오는 item live data
    private val insideSelectedItem = MutableLiveData<RestaurantInfo>()
    val selectedItem: LiveData<RestaurantInfo> = insideSelectedItem


    //현재 유저 상태

    //리스트 가져오기

    //로그아웃

    //탈퇴
}