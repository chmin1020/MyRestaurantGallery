package com.fallTurtle.myrestaurantgallery.usecase.user

import com.fallTurtle.myrestaurantgallery.data.repository.user.UserRepository
import javax.inject.Inject

/**
 * Created by 최제민 on 2023-03-22.
 */
class UserCheckUseCase
    @Inject constructor(private val repository: UserRepository){
    operator fun invoke() = repository.getLoginCompleteAnswer()
}