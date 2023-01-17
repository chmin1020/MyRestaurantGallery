package com.fallTurtle.myrestaurantgallery.etc

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

/**
 * 네트워크 연결이 필요한 작업 시, 연결 상태를 확인하기 위한 객체
 * 안드로이드에서 제공하는 ConnectivityManager 객체를 통해 네트워크 상태를 확인한다.
 */
object NetworkWatcher {
    fun checkNetworkState(context: Context): Boolean {
        //인터넷 연결에 대한 속성을 관리하는 매니저 객체
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        //현재 네트워크 연결 중이 아니거나, 네트워크가 인터넷 연결이 안되어 있으면 false
        val currentActiveNet = connectivityManager.activeNetwork ?: return false
        val netAbility = connectivityManager.getNetworkCapabilities(currentActiveNet) ?: return false

        //와이파이 또는 셀룰러 네트워크를 통해 연결 중이라면 true
        return when {
            netAbility.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            netAbility.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            else -> false
        }
    }
}