package com.fallTurtle.myrestaurantgallery.model.retrofit.Interface

import com.fallTurtle.myrestaurantgallery.model.retrofit.response.LocationSearch
import com.fallTurtle.myrestaurantgallery.model.retrofit.values.APIConstant
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

/**
 * retrofit2 API 를 통해 정보를 가져오려 하는 인터페이스.
 * 이 앱에서는 GET 요청만 필요하므로 Get 요청에 해당하는 함수만 가진다.
 */
interface LocationSearchInterface {
    //GET 명령을 해당 url 에게 보내서 searchLocation 정보를 얻어오는 함수를 정의한다. (LocationResponse 객체로 받아옴)
    @GET(APIConstant.GET_KAKAOMAP_LOCATION)
    suspend fun getSearchLocationOfRestaurants(
        @Header("Authorization") Authorization: String = APIConstant.KAKAO_API_KEY,
        @Query("query") query: String,
        @Query("page") page: Int
        ): Response<LocationSearch>
}