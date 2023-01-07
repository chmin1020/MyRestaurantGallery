package com.fallTurtle.myrestaurantgallery.repository

import com.fallTurtle.myrestaurantgallery.model.firebase.FirebaseUtils
import com.fallTurtle.myrestaurantgallery.model.room.Info
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.storage.StorageReference
import java.io.FileInputStream

class FirebaseDataRepository {
    private fun storeRef(): DocumentReference = FirebaseUtils.getStoreRef()
    private fun storageRef(): StorageReference = FirebaseUtils.getStorageRef()

    fun addNewItem(item: Info) {
        storeRef().collection("restaurants").document(item.dbID).set(item)
    }

    fun deleteItem(item: Info) {
        item.image?.let { storageRef().child(it).delete() }
        storeRef().collection("restaurants").document(item.dbID).delete()
    }

    fun getImageRef(img: String): StorageReference {
        return storageRef().child(img)
    }

    fun addNewImage(img: String, stream: FileInputStream){
        storageRef().child(img).putStream(stream)
    }

    fun deleteImage(img: String){
        storageRef().child(img).delete()
    }
}