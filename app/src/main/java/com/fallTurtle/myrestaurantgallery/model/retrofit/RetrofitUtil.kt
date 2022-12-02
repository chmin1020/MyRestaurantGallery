package com.fallTurtle.myrestaurantgallery.model.retrofit

import com.fallTurtle.myrestaurantgallery.BuildConfig
import com.fallTurtle.myrestaurantgallery.model.retrofit.values.Url
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * 사용할 Retrofit2 객체를 가져오는 object.
 * 기본적으로 apiService 를 사용하고, 이 인터페이스를 위해 retrofit 객체를 만든다.
 * retrofit 객체는 편리하게 REST API 를 활용하기 위해 사용한다.
 */
object RetrofitUtil {
    // 후에 http GET 요청을 보내기 위해 사용할 서비스 인터페이스 실현 객체
    val apiService: APIService by lazy { getRetrofit().create(APIService::class.java) }

    /* Retrofit 객체를 만들어서 반환하는 함수 */
    private fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(Url.KAKAOMAP_URL) //
            .addConverterFactory(GsonConverterFactory.create()) // json 을 받아서 gson 으로 파싱
            .build()
    }
}