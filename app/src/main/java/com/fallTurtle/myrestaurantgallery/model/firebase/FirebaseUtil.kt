package com.fallTurtle.myrestaurantgallery.model.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

object FirebaseUtil {
    private val mAuth = FirebaseAuth.getInstance()
    private val db = Firebase.firestore
    private val str = Firebase.storage
}