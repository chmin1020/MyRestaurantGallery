package com.fallTurtle.myrestaurantgallery.repository.item.data

import com.fallTurtle.myrestaurantgallery.model.firebase.FirebaseUtils
import com.fallTurtle.myrestaurantgallery.model.room.Info
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class FireStoreRepository {
    private fun collectionRef(): CollectionReference = FirebaseUtils.getStoreRef().collection("restaurants")

    fun insertData(item: Info){
        collectionRef().document(item.dbID).set(item)
    }

    fun deleteData(item: Info){
        collectionRef().document(item.dbID).delete()
    }

    suspend fun getAllDataInStore(): QuerySnapshot? {
        return suspendCoroutine { continuation ->
            collectionRef().get().addOnCompleteListener {
                if(it.isSuccessful) continuation.resume(it.result)
                else continuation.resume(null)
            }
        }
    }
}