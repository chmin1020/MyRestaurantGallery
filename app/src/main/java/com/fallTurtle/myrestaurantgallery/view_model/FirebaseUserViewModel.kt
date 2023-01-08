package com.fallTurtle.myrestaurantgallery.view_model

import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.fallTurtle.myrestaurantgallery.model.room.Info
import com.fallTurtle.myrestaurantgallery.repository.FirebaseUserRepository
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import kotlinx.coroutines.launch

class FirebaseUserViewModel(application: Application) : AndroidViewModel(application) {
    //파이어베이스 전용 리포지토리
    private val userRepository = FirebaseUserRepository().also { it.updateUser() }
    val userState:LiveData<Boolean> = userRepository.getUserState()

    fun getOptionForLogin(request: String): GoogleSignInOptions{
        return userRepository.getOptionForLogin(request)
    }

    fun getTokenForLogin(result: Intent?): String? {
        return userRepository.getTokenForLogin(result)
    }

    fun loginUser(idToken: String){
        viewModelScope.launch { userRepository.finalLoginWithCredential(idToken) }
    }

    fun logoutUser(){
        viewModelScope.launch { userRepository.logoutUser() }
    }

    fun withdrawUser(deletingItems: List<Info>?){
        viewModelScope.launch { userRepository.withDrawUser(deletingItems) }
    }
}