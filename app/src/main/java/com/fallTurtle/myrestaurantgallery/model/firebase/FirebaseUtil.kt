package com.fallTurtle.myrestaurantgallery.model.firebase

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

object FirebaseUtil {
    private val mAuth = FirebaseAuth.getInstance()
    private val str = Firebase.storage

    fun getFirestore(context: Context): FirebaseFirestore{
        return Firebase.firestore
    }
}