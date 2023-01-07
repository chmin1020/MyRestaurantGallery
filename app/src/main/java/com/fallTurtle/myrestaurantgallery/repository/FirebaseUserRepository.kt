package com.fallTurtle.myrestaurantgallery.repository

import androidx.lifecycle.MutableLiveData
import com.fallTurtle.myrestaurantgallery.model.firebase.FirebaseUtils
import com.fallTurtle.myrestaurantgallery.model.room.Info
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import java.io.FileInputStream

/**
 * Firebase 기능을 사용할 수 있게 돕는 핸들러.
 * 이 코드에서 사용하는 2가지 기능(fire store, storage)의 레퍼런스를 저장하고 전달한다.
 * 이를 위해 필요한 현재 유저(FirebaseUser)에 대한 정보에 대해서도 마찬가지의 기능을 가진다.
 **/
class FirebaseUserRepository {
    /* 현재 유저 정보를 새롭게 갱신하는 함수 */
    fun updateUser(){
        FirebaseUtils.updateUser()
    }

    /* 앱에서 로그아웃하는 함수 */
    fun logoutUser() {
        FirebaseUtils.getAuth().signOut()
    }

    /* 앱에서 사용자 탈퇴하는 함수 */
    fun withDrawUser(){
        //현재 유저의 저장 데이터를 담기 위한 document(이메일로 구분) 자체를 제거
        FirebaseUtils.getStoreRef().delete()
        FirebaseUtils.getStorageRef().delete()

        //유저 자체를 파이어베이스 시스템 내부에서 삭제 (실패 시 error 토스트 메시지 출력)
        FirebaseUtils.getUser()?.delete()?.addOnCompleteListener{ task->
            if(task.isSuccessful) FirebaseAuth.getInstance().signOut()
        }
    }
}