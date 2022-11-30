package com.fallTurtle.myrestaurantgallery.model.retrofit

import com.fallTurtle.myrestaurantgallery.model.retrofit.response.LocationResponse
import com.fallTurtle.myrestaurantgallery.model.retrofit.values.Key
import com.fallTurtle.myrestaurantgallery.model.retrofit.values.Url
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

/**
 * retrofit2 API 를 통해 정보를 가져오려 하는 인터페이스.
 * 이 앱에서는 GET 요청만 필요하므로 Get 요청에 해당하는 함수만 가진다.
 */
interface APIService {
    //GET 명령을 해당 url 에게 보내서 searchLocation 정보를 얻어오는 함수를 정의한다. (LocationResponse 객체로 받아옴)
    @GET(Url.GET_KAKAOMAP_LOCATION)
    suspend fun getSearchLocation(
        @Header("Authorization") Authorization: String = Key.KAKAO_API,
        @Query("query") query: String,
        @Query("page") page: Int
        ): Response<LocationResponse>
}