package com.fallTurtle.myrestaurantgallery.repository.user

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.fallTurtle.myrestaurantgallery.model.firebase.FirebaseUtils
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * Firebase 유저 관련 기능을 수행하는 리포지토리.
 * Firebase 구글 인증 방식으로 기능을 수행한다.
 **/
class FirebaseUserRepository: UserRepository {
    private val loginComplete: MutableLiveData<Boolean> = MutableLiveData<Boolean>()


    //---------------------------------------
    // 초기화 함수 (유저 상태 일단 갱신)

    init { updateUserState() }


    //------------------------------------------
    // 오버라이딩 영역

    /* 유저 로그인 상태 확인 함수 */
    override fun getLoginCompleteAnswer(): LiveData<Boolean> = loginComplete

    /* 로그인 함수 (by token) */
    override suspend fun loginUser(idToken: String){
        suspendCoroutine<Any?> { continuation ->
            //Token 보내서 credential 받고 인증 시도
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            FirebaseUtils.getAuth().signInWithCredential(credential).addOnCompleteListener {
                if(it.isSuccessful) //성공 시 현재 유저 상태 갱신
                    updateUserState()
                continuation.resume(null)
            }
        }
    }

    /* 앱에서 로그아웃하는 함수 */
    override suspend fun logoutUser() {
        withContext(Dispatchers.IO){ FirebaseUtils.getAuth().signOut() }
    }

    /* 앱에서 사용자 탈퇴하는 함수 */
    override suspend fun withDrawUser(){
        suspendCoroutine<Any?>{ continuation ->
            //현재 유저의 저장 데이터를 담은 레퍼런스들을 제거
            FirebaseUtils.getStoreRef().delete()
            FirebaseUtils.getStorageRef().delete()

            //유저를 파이어베이스 시스템 내부에서 삭제
            FirebaseUtils.getUser()?.delete()?.addOnCompleteListener { task ->
                if (task.isSuccessful) FirebaseUtils.getAuth().signOut()
                continuation.resume(null)
            }
        }
    }


    //------------------------------------------
    // 내부 함수 영역

    /* 현재 유저 상태를 갱신하는 함수 */
    private fun updateUserState(){
        FirebaseUtils.updateUserState()
        loginComplete.postValue(FirebaseUtils.getUser() != null)
    }
}