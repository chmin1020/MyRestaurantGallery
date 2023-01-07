package com.fallTurtle.myrestaurantgallery.view_model

import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.fallTurtle.myrestaurantgallery.model.room.Info
import com.fallTurtle.myrestaurantgallery.repository.FirebaseUserRepository
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import kotlinx.coroutines.launch

class FirebaseUserViewModel(application: Application) : AndroidViewModel(application) {
    //파이어베이스 전용 리포지토리
    private val userRepository = FirebaseUserRepository()

    fun checkUser(): Boolean {
        userRepository.updateUser()
        return userRepository.isUserExist()
    }

    fun getTokenForLogin(result: Intent?): String? {
        return userRepository.getTokenForLogin(result)
    }

    fun loginUser(idToken: String, job: OnCompleteListener<AuthResult>){
        viewModelScope.launch { userRepository.finalLoginWithCredential(idToken, job) }
    }

    fun logoutUser(){
        viewModelScope.launch { userRepository.logoutUser() }
    }

    fun withdrawUser(deletingItems: List<Info>?){
        viewModelScope.launch { userRepository.withDrawUser(deletingItems) }
    }
}