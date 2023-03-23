package com.fallTurtle.myrestaurantgallery.usecase.item

import com.fallTurtle.myrestaurantgallery.data.repository.item.ItemRepository
import javax.inject.Inject

/**
 * Created by 최제민 on 2023-03-23.
 */
class ItemRestoreUseCase
    @Inject constructor(private val itemRepository: ItemRepository) {

    suspend operator fun invoke(){
        itemRepository.restorePreviousItem()
    }
}