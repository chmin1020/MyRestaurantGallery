package com.fallTurtle.myrestaurantgallery.usecase.item

import android.net.Uri
import com.fallTurtle.myrestaurantgallery.data.repository.item.ItemRepository
import com.fallTurtle.myrestaurantgallery.data.room.RestaurantInfo
import javax.inject.Inject

/**
 * Created by 최제민 on 2023-03-22.
 */
class ItemInsertUseCase
    @Inject constructor(private val repository: ItemRepository){
        suspend operator fun invoke(item: RestaurantInfo, uri: Uri?)
        = repository.itemInsert(item, uri)
}