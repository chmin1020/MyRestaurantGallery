package com.fallTurtle.myrestaurantgallery.utility

import com.fallTurtle.myrestaurantgallery.response.LocationResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface APIService {
    @GET(Url.GET_KAKAOMAP_LOCATION)
    suspend fun getSearchLocation(
        @Header("Authorization") Authorization: String = Key.KAKAO_API,
        @Query("query") query: String,
        @Query("page") page: Int
        ): Response<LocationResponse>
}