package com.fallTurtle.myrestaurantgallery.ui.view_model

import android.app.Application
import androidx.lifecycle.*
import com.fallTurtle.myrestaurantgallery.data.repository.user.FirebaseUserRepository
import com.fallTurtle.myrestaurantgallery.data.repository.user.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UserViewModel : ViewModel() {
    //유저 비즈니스 로직 리포지토리
    private val userRepository: UserRepository = FirebaseUserRepository()


    //----------------------------------------------------
    // 라이브 데이터 프로퍼티 영역

    //아이템 처리 진행 여부
    private val insideProgressing = MutableLiveData(false)
    val progressing: LiveData<Boolean> = insideProgressing

    //아이템 처리 종료 여부 (액티비티 내에서 필요 시 체크)
    private val insideFinish = MutableLiveData(false)
    val workFinishFlag: LiveData<Boolean> = insideFinish

    //유저 존재 상태
//    val loginCompleteAnswer: LiveData<Boolean> = userRepository.getLoginCompleteAnswer()
//

    //----------------------------------------------------
    // 함수 영역 (유저 작업)

    /* 토큰을 통한 최종 인증된 로그인을 시도하는 함수 */
    fun loginUser(idToken: String){
        viewModelScope.launch(Dispatchers.IO) {
            insideProgressing.postValue(true)
            userRepository.loginUser(idToken)
            insideProgressing.postValue(false)
        }
    }

    /* 로그아웃하는 함수 */
    fun logoutUser(){
        viewModelScope.launch(Dispatchers.IO) {
            insideProgressing.postValue(true)
            userRepository.logoutUser()
            insideProgressing.postValue(false)
            insideFinish.postValue(true)
        }
    }

    /* 회원 탈퇴를 위한 함수. 탈퇴 이전에 유저가 가진 모든 아이템 삭제  */
    fun withdrawUser(){
        viewModelScope.launch(Dispatchers.IO) {
            insideProgressing.postValue(true)
            userRepository.withDrawUser()
            insideProgressing.postValue(false)
            insideFinish.postValue(true)
        }
    }
}