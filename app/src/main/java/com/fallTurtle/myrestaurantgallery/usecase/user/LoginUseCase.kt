package com.fallTurtle.myrestaurantgallery.usecase.user

import com.fallTurtle.myrestaurantgallery.data.repository.user.UserRepository
import javax.inject.Inject

/**
 * Created by 최제민 on 2023-03-22.
 */
class LoginUseCase
    @Inject constructor(private val repository: UserRepository){
    suspend operator fun invoke(idToken: String){
        repository.loginUser(idToken)
    }
}