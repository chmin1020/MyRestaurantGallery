package com.fallTurtle.myrestaurantgallery.view_model

import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.fallTurtle.myrestaurantgallery.model.room.Info
import com.fallTurtle.myrestaurantgallery.repository.FirebaseUserRepository
import kotlinx.coroutines.launch

class FirebaseUserViewModel(application: Application) : AndroidViewModel(application) {
    //유저 비즈니스 로직 리포지토리
    private val userRepository = FirebaseUserRepository().also { it.updateUser() }

    //아이템 처리 진행 여부를 알려주는 boolean 프로퍼티
    private val insideProgressing = MutableLiveData(false)
    val progressing: LiveData<Boolean> = insideProgressing

    //아이템 처리를 한 이후 해당 화면은 종료됨
    private val insideFinish = MutableLiveData(false)
    val finish: LiveData<Boolean> = insideFinish

    //유저 상태 live data -> null 아니라면 로그인 상태로 판단
    val userState:LiveData<Boolean> = userRepository.getUserState()

    /* 로그인을 위한 option, token 값을 반환하는 함수들 */
    fun getOptionForLogin(request: String) = userRepository.getOptionForLogin(request)
    fun getTokenForLogin(result: Intent?) = userRepository.getTokenForLogin(result)

    /* 토큰을 통한 최종 인증된 로그인을 시도하는 함수 */
    fun loginUser(idToken: String){
        viewModelScope.launch {
            insideProgressing.postValue(true)
            userRepository.finalLoginWithCredential(idToken)
            insideProgressing.postValue(false)
        }
    }

    /* 로그아웃하는 함수 */
    fun logoutUser(){
        viewModelScope.launch {
            insideProgressing.postValue(true)
            userRepository.logoutUser()
            insideProgressing.postValue(false)
            insideFinish.postValue(true)
        }
    }

    /* 회원 탈퇴를 위한 함수. 탈퇴 이전에 유저가 가진 모든 아이템 삭제  */
    fun withdrawUser(deletingItems: List<Info>?){
        viewModelScope.launch {
            insideProgressing.postValue(true)
            userRepository.withDrawUser(deletingItems)
            insideProgressing.postValue(false)
            insideFinish.postValue(true)
        }
    }
}