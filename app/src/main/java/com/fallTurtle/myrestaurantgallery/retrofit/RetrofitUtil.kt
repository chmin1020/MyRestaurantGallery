package com.fallTurtle.myrestaurantgallery.retrofit

import com.fallTurtle.myrestaurantgallery.BuildConfig
import com.fallTurtle.myrestaurantgallery.retrofit.values.Url
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * 사용할 Retrofit2 객체를 가져오는 object.
 * 기본적으로 apiService 를 사용하고, 이 인터페이스를 위해 retrofit, okHttpClient 객체를 만든다.
 * 여기서 okHttpClient 객체는 타임아웃과 로그 모니터링을 위해 사용하고,
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
            .client(buildOkHttpClient()) // OkHttp 사용
            .build()
    }

    /* OkHttpClient 객체를 만들어서 반환하는 함수 */
    private fun buildOkHttpClient(): OkHttpClient {
        val interceptor = HttpLoggingInterceptor() // 매번 api 호출 시 마다 내용 모니터링

        if (BuildConfig.DEBUG)
            interceptor.level = HttpLoggingInterceptor.Level.BODY
        else
            interceptor.level = HttpLoggingInterceptor.Level.NONE

        return OkHttpClient.Builder()
            .connectTimeout(5, TimeUnit.SECONDS) // 5초 동안 응답 없으면 에러
            .addInterceptor(interceptor) //만든 interceptor 넣어줌
            .build()
    }
}