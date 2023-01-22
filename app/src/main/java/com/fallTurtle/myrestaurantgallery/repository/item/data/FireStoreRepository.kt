package com.fallTurtle.myrestaurantgallery.repository.item.data

import com.fallTurtle.myrestaurantgallery.model.firebase.FirebaseUtils
import com.fallTurtle.myrestaurantgallery.model.room.Info
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.QuerySnapshot
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class FireStoreRepository: DataRepository {
    private fun collectionRef(): CollectionReference = FirebaseUtils.getStoreRef().collection("restaurants")

    override suspend fun getAllData(): List<Info> {
        return suspendCoroutine { continuation ->
            collectionRef().get().addOnCompleteListener {
                if(it.isSuccessful) continuation.resume(it.result.toObjects(Info::class.java))
                else continuation.resume(listOf())
            }
        }
    }

    override suspend fun getProperData(id: String): Info {
        return suspendCoroutine { continuation ->
            collectionRef().document(id).get().addOnCompleteListener {
                if(it.isSuccessful) continuation.resume(it.result.toObject(Info::class.java) ?: Info())
                else continuation.resume(Info())
            }
        }
    }

    override suspend fun clearData() {
        getAllData().forEach { deleteData(it) }
    }

    override suspend fun insertData(data: Info) {
        suspendCoroutine<Any?> { continuation ->
            collectionRef().document(data.dbID).set(data).addOnCompleteListener { continuation.resume(null) }
        }
    }

    override suspend fun deleteData(data: Info) {
        suspendCoroutine<Any?> { continuation ->
            collectionRef().document(data.dbID).delete().addOnCompleteListener { continuation.resume(null) }
        }
    }
}