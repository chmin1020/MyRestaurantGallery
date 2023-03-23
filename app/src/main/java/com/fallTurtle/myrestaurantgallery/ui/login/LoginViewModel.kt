package com.fallTurtle.myrestaurantgallery.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fallTurtle.myrestaurantgallery.usecase.item.ItemRestoreUseCase
import com.fallTurtle.myrestaurantgallery.usecase.user.LoginUseCase
import com.fallTurtle.myrestaurantgallery.usecase.user.UserCheckUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Created by 최제민 on 2023-03-22.
 */
@HiltViewModel
class LoginViewModel
    @Inject constructor(
        private val userCheck: UserCheckUseCase,
        private val login: LoginUseCase,
        private val restoreItems: ItemRestoreUseCase
        ): ViewModel() {

    //동작 진행중 여부
    private val insideProgressing = MutableLiveData(false)
    val progressing: LiveData<Boolean> = insideProgressing

    private val insideUserExist = MutableLiveData(false)
    val userExist: LiveData<Boolean> = insideUserExist

    init {
        viewModelScope.launch {
            userCheck().collectLatest { insideUserExist.postValue(it) }
        }
    }

    //로그인
    fun loginUser(idToken: String){
        viewModelScope.launch {
            insideProgressing.postValue(true)
            withContext(Dispatchers.IO){
                login(idToken)
                restoreItems()
            }
            insideProgressing.postValue(false)
        }
    }
}