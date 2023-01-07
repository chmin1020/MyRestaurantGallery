package com.fallTurtle.myrestaurantgallery.view_model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.fallTurtle.myrestaurantgallery.model.room.Info
import com.fallTurtle.myrestaurantgallery.repository.FirebaseUserRepository
import kotlinx.coroutines.launch

class FirebaseUserViewModel(application: Application) : AndroidViewModel(application) {
    //파이어베이스 전용 리포지토리
    private val firebaseRepository = FirebaseUserRepository()

    fun updateUser(){
        viewModelScope.launch { firebaseRepository.updateUser() }
    }

    fun logoutUser(){
        viewModelScope.launch { firebaseRepository.logoutUser() }
    }

    fun withdrawUser(deletingItems: List<Info>?){
        viewModelScope.launch { firebaseRepository.withDrawUser(deletingItems) }
    }
}