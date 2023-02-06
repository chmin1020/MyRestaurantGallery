package com.fallTurtle.myrestaurantgallery.repository.map

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Build
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
    private val geocoder: Geocoder by lazy { Geocoder(context) }


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

    /* 좌표를 주소로 변환하는 함수 */
    override suspend fun requestCurrentAddress(location: LocationPair) : String{
        //geocoder 객체를 통해 현재 위도와 경도로 주소 받아오기 시도
        return if(Build.VERSION.SDK_INT >= 33) {
            suspendCoroutine { continuation ->
                geocoder.getFromLocation(location.latitude, location.longitude, 10) {
                    continuation.resume(addressCompleteWork(it.firstOrNull()))
                }
            }
        } else{
            suspendCoroutine { continuation ->
                //blocking 함수를 suspend 영역에서 써서 오류 발생
                //추후 해결해야 할 사항
                geocoder.getFromLocation(location.latitude, location.longitude, 10)?.let {
                    continuation.resume(addressCompleteWork(it.firstOrNull()))
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

    /* human friendly 문자열 형식 주소를 반환하는 함수 */
    private fun addressCompleteWork(addressFragment: Address?): String {
        val addressMaker = StringBuilder()

        //받은 주소가 성공적이라면, 받은 주소 정보를 알맞은 String 형식으로 변환
        addressFragment?.also {
            val str = it.getAddressLine(0).split(" ")
            addressMaker.append(str[1])
            for (num in 2 until str.size)
                addressMaker.append(" ${str[num]}")
        } ?: run { addressMaker.append("주소 찾을 수 없음") }

        return addressMaker.toString()
    }
}