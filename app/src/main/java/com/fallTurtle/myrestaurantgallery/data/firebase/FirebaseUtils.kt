package com.fallTurtle.myrestaurantgallery.data.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object FirebaseUtils {
    //파이어베이스 각 요소들(Auth, User, Store reference, Storage reference)
    private val baseAuth = FirebaseAuth.getInstance()
    private var curUser: FirebaseUser? = null
    private var storeRef: DocumentReference? = null
    private var storageRef: StorageReference? = null

    private val insideUserExist = MutableStateFlow(curUser != null)
    val userExist: StateFlow<Boolean> = insideUserExist

    /* 현재 유저 및 관련 파이어베이스 내부 요소를 새롭게 갱신하는 함수 */
    fun updateUserState(){
        //유저 갱신
        curUser = baseAuth.currentUser
        insideUserExist.value = (curUser != null).also { if(it) resetReferences() }

        //유저 id에 따른 reference 갱신
        curUser?.email?.let {
            storeRef = Firebase.firestore.collection("users").document(it)
            storageRef = Firebase.storage.reference.child(it)
        }
    }

    /* 파이어베이스의 각 요소(Auth, User, Store reference, Storage reference) 제공 함수들 */
    fun getAuth(): FirebaseAuth = baseAuth
    fun getUser(): FirebaseUser? = curUser
    fun getStoreRef(): DocumentReference? = storeRef
    fun getStorageRef(): StorageReference? =  storageRef

    private fun resetReferences(){
        storeRef = null
        storageRef = null
    }
}