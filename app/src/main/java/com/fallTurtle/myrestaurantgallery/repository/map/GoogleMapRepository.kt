package com.fallTurtle.myrestaurantgallery.repository.map

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.app.ActivityCompat
import com.fallTurtle.myrestaurantgallery.model.retrofit.value_object.LocationPair
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.Task
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * Google 맵 관련 기능을 수행하는 리포지토리.
 * 구글 맵 api 방식으로 맵 관련 기능을 구현한다.
 **/
class GoogleMapRepository(application: Application): MapRepository {
    //객체 생성 및 권한 체크 시 필요한 콘텍스트
    private val context = application.baseContext

    //위치를 얻게 도와주는 클라이언트 객체
    private val locationClient: FusedLocationProviderClient by lazy { LocationServices.getFusedLocationProviderClient(context) }


    //----------------------------------------------
    // 오버라이딩 영역

    /* 현재 위치를 가지고 오는 함수 */
    override suspend fun requestCurrentLocation(): LocationPair? {
        return suspendCoroutine { continuation->
            if (checkMapPermission()) {
                val locationTask = locationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, CancellationTokenSource().token)

                locationTask.addOnCompleteListener { task: Task<Location> ->
                    if (task.isSuccessful)
                        continuation.resume(LocationPair(task.result?.latitude ?: -1.0, task.result?.longitude ?: -1.0))
                    else
                        continuation.resume(null)
                }
            }
        }
    }


    //----------------------------------------------
    // 내부 함수 영역

    /* 지도 관련 권한을 확인하는 함수 */
    private fun checkMapPermission() : Boolean {
        return (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
    }
}