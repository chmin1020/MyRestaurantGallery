package com.fallTurtle.myrestaurantgallery.repository.item.image

import android.net.Uri
import com.fallTurtle.myrestaurantgallery.model.firebase.FirebaseUtils
import com.google.firebase.storage.ListResult
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class StorageRepository{
    private val storageRef = FirebaseUtils.getStorageRef()

    suspend fun getAllImagesInStorage(): ListResult? {
        return suspendCoroutine { continuation ->
            storageRef.listAll().addOnCompleteListener {
                if(it.isSuccessful) continuation.resume(it.result)
                else continuation.resume(null)
            }
        }
    }

    suspend fun insertImage(imageName: String, uri: Uri){
        suspendCoroutine<Any?> { continuation ->
            storageRef.child(imageName).putFile(uri).addOnCompleteListener{ continuation.resume(null)}
        }
    }

    suspend fun deleteImage(imageName: String){
        suspendCoroutine<Any?> { continuation ->
            storageRef.child(imageName).delete().addOnCompleteListener { continuation.resume(null) }
        }
    }
}