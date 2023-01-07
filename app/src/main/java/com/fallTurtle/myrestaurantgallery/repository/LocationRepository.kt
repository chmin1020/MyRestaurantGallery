package com.fallTurtle.myrestaurantgallery.repository

import com.fallTurtle.myrestaurantgallery.model.retrofit.Interface.LocationSearchInterface
import com.fallTurtle.myrestaurantgallery.model.retrofit.values.Url
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class LocationRepository {
    /* Retrofit 객체는 singleton 유지 */
    companion object {
        private val retrofit by lazy {
            Retrofit.Builder()
                .baseUrl(Url.KAKAOMAP_URL)
                .addConverterFactory(GsonConverterFactory.create()) // json 을 받아서 gson 으로 파싱
                .build()
        }
    }

    // 후에 http GET 요청을 보내기 위해 사용할 서비스 인터페이스 실현 객체
    private val locationSearchAPI: LocationSearchInterface
        by lazy { retrofit.create(LocationSearchInterface::class.java) }

    /* 쿼리에 해당하는 식당 리스트를 get */
    suspend fun getRestaurantInfo(query: String, page: Int)
    = locationSearchAPI.getSearchLocationOfRestaurants(query = query, page = page)

    /* 쿼리에 해당하는 카페 리스트를 get */
    suspend fun getCafeInfo(query: String, page: Int)
    = locationSearchAPI.getSearchLocationOfCafe(query = query, page = page)
}