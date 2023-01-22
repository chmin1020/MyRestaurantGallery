package com.fallTurtle.myrestaurantgallery.model.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage

object FirebaseUtils {
    //파이어베이스 각 요소들(Auth, User, Store reference, Storage reference)
    private val baseAuth = FirebaseAuth.getInstance()
    private var curUser: FirebaseUser? = null
    private lateinit var storeRef: DocumentReference
    private lateinit var storageRef: StorageReference

    /* 현재 유저 및 관련 파이어베이스 내부 요소를 새롭게 갱신하는 함수 */
    fun updateUserState(){
        curUser = baseAuth.currentUser
        val id = curUser?.email.toString()
        storeRef = Firebase.firestore.collection("users").document(id)
        storageRef = Firebase.storage.reference.child(id)
    }

    /* 파이어베이스의 각 요소(Auth, User, Store reference, Storage reference) 제공 함수들 */
    fun getAuth(): FirebaseAuth = baseAuth
    fun getUser(): FirebaseUser? = curUser
    fun getStoreRef(): DocumentReference = storeRef
    fun getStorageRef(): StorageReference=  storageRef
}