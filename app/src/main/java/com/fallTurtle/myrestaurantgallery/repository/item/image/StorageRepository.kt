package com.fallTurtle.myrestaurantgallery.repository.item.image

import android.net.Uri
import com.fallTurtle.myrestaurantgallery.model.firebase.FirebaseUtils

class StorageRepository{
    private val storageRef = FirebaseUtils.getStorageRef()

    fun getProperReference() = storageRef

    fun insertImage(imageName: String, uri: Uri){
        storageRef.child(imageName).putFile(uri)
    }

    fun deleteImage(imageName: String){
        storageRef.child(imageName).delete()
    }
}