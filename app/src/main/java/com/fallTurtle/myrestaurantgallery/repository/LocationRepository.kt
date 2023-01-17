package com.fallTurtle.myrestaurantgallery.repository

import com.fallTurtle.myrestaurantgallery.model.retrofit.value_object.LocationPair
import com.fallTurtle.myrestaurantgallery.model.retrofit.value_object.LocationResult
import com.fallTurtle.myrestaurantgallery.model.retrofit.Interface.LocationSearchInterface
import com.fallTurtle.myrestaurantgallery.model.retrofit.response.LocationSearch
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

    // 후에 http GET 요청을 보내기 위해 사용할 서비스 인터페이스 실현 객체
    private val locationSearchAPI: LocationSearchInterface
        by lazy { retrofit.create(LocationSearchInterface::class.java) }

    // 검색을 위한 설정 변수, 컬렉션
    private val searchTotalResults = mutableListOf<LocationResult>()
    private var restaurantFinish = false
    private var cafeFinish = false


    /* 식당과 카페를 포함한 전체 검색 결과를 반환하는 함수 */
    suspend fun searchTotalInfo(query: String, page: Int):List<LocationResult> {
        if(page == 1)
            resetSearchSetting()

        //식당과 카페 카테고리로 검색 실시
        val restaurantResponse = searchRestaurantInfo(query, page)
        val cafeResponse = searchCafeInfo(query, page)

        //아직 식당 데이터가 남아 있으면 결과 추가
        if(!isSearchFinish(restaurantResponse, restaurantFinish).also { restaurantFinish = it })
            extractResultsFromResponse(restaurantResponse)

        //아직 카페 데이터가 남아 있으면 결과 추가
        if(!isSearchFinish(cafeResponse, cafeFinish).also { cafeFinish = it })
            extractResultsFromResponse(cafeResponse)

        return searchTotalResults
    }

    /* 쿼리에 해당하는 식당 리스트를 get */
    private suspend fun searchRestaurantInfo(query: String, page: Int)
    = locationSearchAPI.getSearchLocationOfRestaurants(query = query, page = page)

    /* 쿼리에 해당하는 카페 리스트를 get */
    private suspend fun searchCafeInfo(query: String, page: Int)
    = locationSearchAPI.getSearchLocationOfCafe(query = query, page = page)

    /* 검색 설정 초기화하는 함수 */
    private fun resetSearchSetting(){
        searchTotalResults.clear()
        restaurantFinish = false
        cafeFinish = false
    }

    /* 검색할 수 있는 상황인지 체크하는 함수 */
    private fun isSearchFinish(response: Response<LocationSearch>, searchEnd: Boolean)
        = response.isSuccessful && (searchEnd || (response.body()?.meta?.is_end ?: true))

    /* 검색 결과를 response 내에서 추출하여 리스트에 추가하는 함수 */
    private fun extractResultsFromResponse(response: Response<LocationSearch>){
        response.body()?.let { searchContent ->
            searchContent.documents.forEach {
                val locationPair = LocationPair(it.y.toDouble(), it.x.toDouble())
                searchTotalResults.add(LocationResult(it.address_name, it.place_name, it.category_name, locationPair))
            }
        }
    }
}