package com.fallTurtle.myrestaurantgallery.usecase.location_search

import com.fallTurtle.myrestaurantgallery.di.RetrofitRepositoryForLocation
import com.fallTurtle.myrestaurantgallery.repository.location.LocationRepository
import javax.inject.Inject

/**
 * Created by 최제민 on 2023-03-22.
 */
class LocationSearchUseCase
    @Inject constructor(private val repository: LocationRepository){
    suspend operator fun invoke(query: String, page: Int) =
        repository.searchTotalInfo(query, page)
}