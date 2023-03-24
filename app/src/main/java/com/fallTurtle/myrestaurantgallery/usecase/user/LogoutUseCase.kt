package com.fallTurtle.myrestaurantgallery.usecase.user

import com.fallTurtle.myrestaurantgallery.data.repository.item.ItemRepository
import com.fallTurtle.myrestaurantgallery.data.repository.user.UserRepository
import javax.inject.Inject

/**
 * Created by 최제민 on 2023-03-22.
 */
class LogoutUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val itemRepository: ItemRepository
){
    suspend operator fun invoke(){
        itemRepository.localItemClear()
        userRepository.logoutUser()
    }
}