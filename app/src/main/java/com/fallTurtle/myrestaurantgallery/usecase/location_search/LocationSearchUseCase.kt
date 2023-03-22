package com.fallTurtle.myrestaurantgallery.usecase.location_search

import com.fallTurtle.myrestaurantgallery.repository.location.LocationRepository
import com.fallTurtle.myrestaurantgallery.repository.location.RetrofitLocationRepository

/**
 * Created by 최제민 on 2023-03-22.
 */
class LocationSearchUseCase {
    private val locationRepository: LocationRepository = RetrofitLocationRepository()

    //검색 결과 flow
    suspend operator fun invoke(query: String, page: Int) =
        locationRepository.searchTotalInfo(query, page)
}