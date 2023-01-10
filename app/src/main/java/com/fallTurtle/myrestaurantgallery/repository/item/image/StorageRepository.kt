package com.fallTurtle.myrestaurantgallery.repository.item.image

import android.net.Uri
import com.fallTurtle.myrestaurantgallery.model.firebase.FirebaseUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class StorageRepository{
    private val storageRef = FirebaseUtils.getStorageRef()

    fun getProperReference() = storageRef

    suspend fun insertImage(imageName: String, uri: Uri){
        withContext(Dispatchers.IO){ storageRef.child(imageName).putFile(uri) }
    }

    suspend fun deleteImage(imageName: String){
        withContext(Dispatchers.IO){ storageRef.child(imageName).delete() }
    }
}