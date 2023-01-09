package com.fallTurtle.myrestaurantgallery.repository.item.image

import com.fallTurtle.myrestaurantgallery.model.firebase.FirebaseUtils
import java.io.FileInputStream

class StorageRepository{
    private val storageRef = FirebaseUtils.getStorageRef()

    fun addNewImage(imageName: String, stream: FileInputStream){
        //item.image?.let { storageRepository.deleteImage(it) }
    }

    fun deleteImage(imageName: String){
        storageRef.child(imageName).delete()
    }
}