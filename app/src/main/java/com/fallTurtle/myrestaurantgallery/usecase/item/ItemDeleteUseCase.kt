package com.fallTurtle.myrestaurantgallery.usecase.item

import com.fallTurtle.myrestaurantgallery.data.repository.item.ItemRepository
import javax.inject.Inject

/**
 * Created by 최제민 on 2023-03-22.
 */
class ItemDeleteUseCase
    @Inject constructor(private val repository: ItemRepository){
        suspend operator fun invoke(id: String) = repository.itemDelete(id)
}