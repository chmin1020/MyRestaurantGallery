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
 * 기본적으로 apiService를 사용하고, 이 인터페이스를 위해 retrofit, okHttpClient 객체를 만든다.
 */
object RetrofitUtil {
    val apiService: APIService by lazy { getRetrofit().create(APIService::class.java) }

    private fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(Url.KAKAOMAP_URL)
            .addConverterFactory(GsonConverterFactory.create()) // gson으로 파싱
            .client(buildOkHttpClient()) // OkHttp 사용
            .build()
    }

    private fun buildOkHttpClient(): OkHttpClient {
        val interceptor = HttpLoggingInterceptor() // 매번 api 호출 시 마다 로그 확인 할것
        if (BuildConfig.DEBUG) {
            interceptor.level = HttpLoggingInterceptor.Level.BODY
        } else {
            interceptor.level = HttpLoggingInterceptor.Level.NONE
        }
        return OkHttpClient.Builder()
            .connectTimeout(5, TimeUnit.SECONDS) // 5초 동안 응답 없으면 에러
            .addInterceptor(interceptor)
            .build()
    }
}