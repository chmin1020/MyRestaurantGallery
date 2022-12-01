package com.fallTurtle.myrestaurantgallery.model.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage

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

        fun updateUserId(){
            curUser = mAuth.currentUser

            val id = curUser?.email.toString()
            storeRef = Firebase.firestore.collection("users").document(id)
            storageRef = Firebase.storage.reference.child(id)
        }

        fun getUser(): FirebaseUser?{
            return curUser
        }

        fun getFirestoreRef(): DocumentReference{
            return storeRef
        }

        fun getStorageRef(): StorageReference{
            return storageRef
        }
    }
 }