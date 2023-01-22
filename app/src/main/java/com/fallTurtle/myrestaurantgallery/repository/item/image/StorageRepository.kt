package com.fallTurtle.myrestaurantgallery.repository.item.image

import android.net.Uri
import com.fallTurtle.myrestaurantgallery.model.firebase.FirebaseUtils
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class StorageRepository: ImageRepository{
    private val storageRef = FirebaseUtils.getStorageRef()

    override suspend fun clearImages() {
        val images = getAllImages()
        images.forEach { deleteImage(it) }
    }

    override suspend fun insertImage(imageName: String, uri: Uri):String?{
        suspendCoroutine<Any?> { continuation ->
            storageRef.child(imageName).putFile(uri).addOnCompleteListener{ continuation.resume(null)}
        }

        return suspendCoroutine { continuation ->
            storageRef.child(imageName).downloadUrl.addOnCompleteListener {
                if(it.isSuccessful)
                    continuation.resume(it.result.toString())
            }
        }

    }

    override suspend fun deleteImage(imageName: String){
        suspendCoroutine<Any?> { continuation ->
            storageRef.child(imageName).delete().addOnCompleteListener { continuation.resume(null) }
        }
    }


    private suspend fun getAllImages(): List<String> {
        return suspendCoroutine { continuation ->
            val images = mutableListOf<String>()
            storageRef.listAll().addOnCompleteListener {
                if(it.isSuccessful){
                    it.result.items.forEach{ ref -> images.add(ref.name) }
                }
                continuation.resume(images)
            }
        }
    }
}