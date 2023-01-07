package com.fallTurtle.myrestaurantgallery.model.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage

abstract class FirebaseUtils {
    companion object{
        private val mAuth = FirebaseAuth.getInstance()
        private var curUser: FirebaseUser? = null
        private lateinit var storeRef: DocumentReference
        private lateinit var storageRef: StorageReference

        /* 현재 유저 정보를 새롭게 갱신하는 함수 */
        fun updateUser(){
            curUser = mAuth.currentUser
            val id = curUser?.email.toString()
            storeRef = Firebase.firestore.collection("users").document(id)
            storageRef = Firebase.storage.reference.child(id)
        }

        fun getAuth(): FirebaseAuth = mAuth
        fun getUser(): FirebaseUser? = curUser
        fun getStoreRef(): DocumentReference = storeRef
        fun getStorageRef(): StorageReference=  storageRef
    }
}