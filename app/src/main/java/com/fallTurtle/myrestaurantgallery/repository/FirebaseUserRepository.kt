package com.fallTurtle.myrestaurantgallery.repository

import android.util.Log
import com.fallTurtle.myrestaurantgallery.model.firebase.FirebaseUtils
import com.fallTurtle.myrestaurantgallery.model.room.Info

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
    fun withDrawUser(deletingItems: List<Info>?){
        //현재 유저의 저장 데이터 각각 제거(Firestore 특성 상)
        deletingItems?.forEach {
            FirebaseUtils.getStoreRef().collection("restaurants").document(it.dbID).delete()
            it.image?.let{ path -> FirebaseUtils.getStorageRef().child(path).delete() }
        }

        //현재 유저의 저장 데이터를 담은 레퍼런스들을 제거
        FirebaseUtils.getStoreRef().delete()
        FirebaseUtils.getStorageRef().delete()

        //유저를 파이어베이스 시스템 내부에서 삭제
        FirebaseUtils.getUser()?.delete()?.addOnCompleteListener{ task->
            if(task.isSuccessful) FirebaseUtils.getAuth().signOut()
        }
    }
}