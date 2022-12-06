package com.fallTurtle.myrestaurantgallery.model.firebase

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import java.io.File

/**
 * Firebase 기능을 사용할 수 있게 돕는 핸들러. (객체 생성을 막기 위해 추상 클래스)
 * 이 코드에서 사용하는 2가지 기능(fire store, storage)의 레퍼런스를 저장하고 전달한다.
 * 이를 위해 필요한 현재 유저(FirebaseUser)에 대한 정보에 대해서도 마찬가지의 기능을 가진다.
 **/
abstract class FirebaseHandler {
    companion object{
        private val mAuth = FirebaseAuth.getInstance()
        private var curUser: FirebaseUser? = null
        private lateinit var storeRef: DocumentReference
        private lateinit var storageRef: StorageReference

        /* 현재 유저를 새롭게 받아오고, 이를 기반으로 id와 ref를 갱신하는 함수 */
        fun updateUserId(){
            curUser = mAuth.currentUser

            val id = curUser?.email.toString()
            storeRef = Firebase.firestore.collection("users").document(id)
            storageRef = Firebase.storage.reference.child(id)
        }

        /* 현재 유저를 반환하는 함수 */
        fun getUser(): FirebaseUser?{
            return curUser
        }

        /* 현재 database reference를 반환하는 함수 */
        fun getFirestoreRef(): DocumentReference{
            return storeRef
        }

        /* 현재 이미지 저장 storage reference를 반환하는 함수 */
        fun getStorageRef(): StorageReference{
            return storageRef
        }

        /* 앱에서 로그아웃 하는 함수 */
        fun logout() {
            mAuth.signOut()
        }

        /* */
        fun tryGetImage(context: Context, fid: String): Pair<Boolean, File>{
            var isExist = false
            val localFile = File(context.cacheDir, fid)

            if(localFile.exists()){
                isExist = true
            }
            else{
                storageRef.child(fid).getFile(localFile).addOnSuccessListener { isExist = true }
            }
            return Pair(isExist, localFile)
        }
    }
 }