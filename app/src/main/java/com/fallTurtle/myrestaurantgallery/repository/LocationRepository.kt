package com.fallTurtle.myrestaurantgallery.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.fallTurtle.myrestaurantgallery.model.retrofit.Interface.LocationSearchInterface
import com.fallTurtle.myrestaurantgallery.model.retrofit.response.LocationResponse
import com.fallTurtle.myrestaurantgallery.model.retrofit.values.Url
import retrofit2.Response
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

    private val searchResponse = MutableLiveData<Array<Response<LocationResponse>>>()

    // 후에 http GET 요청을 보내기 위해 사용할 서비스 인터페이스 실현 객체
    private val locationSearchAPI: LocationSearchInterface
        by lazy { retrofit.create(LocationSearchInterface::class.java) }

    /* 검색 결과 확인 함수 */
    fun getSearchResponse(): LiveData<Array<Response<LocationResponse>>> = searchResponse

    /* 식당과 카페를 포함한 전체 검색 결과를 반환하는 함수 */
    suspend fun searchTotalInfo(query: String, page: Int) {
        //식당과 카페 카테고리로 검색 실시
        val restaurantResponse = searchRestaurantInfo(query, page)
        val cafeResponse = searchCafeInfo(query, page)

        if (restaurantResponse.isSuccessful && cafeResponse.isSuccessful)
            searchResponse.postValue(arrayOf(restaurantResponse, cafeResponse))
    }

    /* 쿼리에 해당하는 식당 리스트를 get */
    private suspend fun searchRestaurantInfo(query: String, page: Int)
    = locationSearchAPI.getSearchLocationOfRestaurants(query = query, page = page)

    /* 쿼리에 해당하는 카페 리스트를 get */
    private suspend fun searchCafeInfo(query: String, page: Int)
    = locationSearchAPI.getSearchLocationOfCafe(query = query, page = page)
}