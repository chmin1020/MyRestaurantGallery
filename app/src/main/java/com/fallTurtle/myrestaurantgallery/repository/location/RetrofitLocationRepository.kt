package com.fallTurtle.myrestaurantgallery.repository.location

import com.fallTurtle.myrestaurantgallery.model.retrofit.value_object.LocationPair
import com.fallTurtle.myrestaurantgallery.model.retrofit.value_object.LocationInfo
import com.fallTurtle.myrestaurantgallery.model.retrofit.Interface.LocationSearchInterface
import com.fallTurtle.myrestaurantgallery.model.retrofit.response.LocationSearch
import com.fallTurtle.myrestaurantgallery.model.retrofit.value_object.APIConstant
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Retrofit 통신을 통해 위치 검색을 수행하는 리포지토리.
 * 통신 대상은 카카오 위치 검색 api.
 **/
class RetrofitLocationRepository: LocationRepository {
    /* Retrofit 객체는 singleton 유지 */
    companion object {
        private val retrofit by lazy {
            Retrofit.Builder()
                .baseUrl(APIConstant.KAKAOMAP_URL)
                .addConverterFactory(GsonConverterFactory.create()) // json 을 받아서 gson 으로 파싱
                .build()
        }
    }

    // 후에 http GET 요청을 보내기 위해 사용할 서비스 인터페이스 객체
    private val locationSearchAPI by lazy { retrofit.create(LocationSearchInterface::class.java) }

    // 검색을 위한 설정 변수, 컬렉션
    private val searchTotalResults = mutableListOf<LocationInfo>()
    private var searchFinish = false


    //---------------------------------------------
    // 오버라이딩 영역

    /* 전체 검색 결과를 받는 함수 */
    override suspend fun searchTotalInfo(query: String, page: Int):List<LocationInfo> {
        if(page == 1) resetSearchSetting()

        //검색 후 결과 추가
        val searchResponses = searchRestaurantInfo(query, page)
        if(!searchFinish) extractResultsFromResponse(searchResponses)

        //검색 개수는 페이지 3번으로 제한
        if(searchResponses.isSuccessful && searchResponses.body()?.meta?.is_end == true)
            searchFinish = true

        return searchTotalResults
    }


    //---------------------------------------------
    // 내부 함수 영역 (검색 결과 저장 수행 관련)

    /* 쿼리에 해당하는 위치 검색 리스트 얻는 함수 */
    private suspend fun searchRestaurantInfo(query: String, page: Int)
    = locationSearchAPI.getSearchLocationOfRestaurants(query = query, page = page)

    /* 검색 결과를 response 내에서 추출 -> 리스트 추가하는 함수 */
    private fun extractResultsFromResponse(response: Response<LocationSearch>){
        response.body()?.let { searchContent ->
            searchContent.documents.forEach {
                val locationPair = LocationPair(it.y.toDouble(), it.x.toDouble())
                searchTotalResults.add(LocationInfo(it.address_name, it.place_name, it.category_name, locationPair))
            }
        }
    }


    //---------------------------------------------
    // 내부 함수 영역 (설정 관련)

    /* 검색 설정 초기화하는 함수 */
    private fun resetSearchSetting(){
        searchTotalResults.clear()
        searchFinish = false
    }
}