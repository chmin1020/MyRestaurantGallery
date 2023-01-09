package com.fallTurtle.myrestaurantgallery.repository.item.data

import com.fallTurtle.myrestaurantgallery.model.firebase.FirebaseUtils
import com.fallTurtle.myrestaurantgallery.model.room.Info
import com.google.firebase.firestore.CollectionReference

class FireStoreRepository {
    private fun collectionRef(): CollectionReference = FirebaseUtils.getStoreRef().collection("restaurants")

    fun insertData(item: Info){
        collectionRef().document(item.dbID).set(item)
    }

    fun deleteData(item: Info){
        collectionRef().document(item.dbID).delete()
    }

    fun getProperCollection(): CollectionReference{
        return collectionRef()
    }
}