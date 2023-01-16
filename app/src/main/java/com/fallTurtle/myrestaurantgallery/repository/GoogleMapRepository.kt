package com.fallTurtle.myrestaurantgallery.repository

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import androidx.core.app.ActivityCompat
import com.fallTurtle.myrestaurantgallery.model.retrofit.etc.LocationPair
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.Task
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class GoogleMapRepository(application: Application) {
    private val context = application.baseContext

    private val locationClient: FusedLocationProviderClient by lazy { LocationServices.getFusedLocationProviderClient(context) }
    private val geocoder: Geocoder by lazy { Geocoder(context) }


    /* 현재 위치를 가지고 오는 함수 */
    suspend fun requestCurrentLocation(): LocationPair? {
        return suspendCoroutine { continuation->
            if (checkMapPermission()) {
                val locationTask = locationClient.getCurrentLocation(PRIORITY_HIGH_ACCURACY, CancellationTokenSource().token)

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
    fun requestCurrentAddress(location: LocationPair) : String{
        val addressMaker = StringBuilder()

        //geocoder 객체를 통해 현재 위도와 경도로 주소 받아오기 시도
        geocoder.getFromLocation(location.latitude, location.longitude, 10)?.let{
            if(it.isEmpty())
                addressMaker.append("주소 찾을 수 없음")
            else{ //받은 주소가 성공적이라면, 받은 리스트를 String 형식으로 변환
                val str = it[0].getAddressLine(0).split(" ")
                addressMaker.append(str[1])
                for(num in 2 until str.size)
                    addressMaker.append(" ${str[num]}")
            }
        }

        return addressMaker.toString()
    }


    /* 지도 관련 권한을 확인하는 함수 */
    private fun checkMapPermission() : Boolean {
        return (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
    }
}