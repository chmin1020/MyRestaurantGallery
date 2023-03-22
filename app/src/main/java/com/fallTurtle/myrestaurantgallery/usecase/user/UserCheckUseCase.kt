package com.fallTurtle.myrestaurantgallery.usecase.user

import com.fallTurtle.myrestaurantgallery.repository.user.FirebaseUserRepository
import com.fallTurtle.myrestaurantgallery.repository.user.UserRepository

/**
 * Created by 최제민 on 2023-03-22.
 */
class UserCheckUseCase {
    private val userRepository: UserRepository = FirebaseUserRepository()

    operator fun invoke() = userRepository.getLoginCompleteAnswer()
}