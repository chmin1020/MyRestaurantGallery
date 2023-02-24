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
        //인터넷 연결에 매니저 객체
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        //현재 인터넷 사용이 불가 하면 false
        val currentActiveNet = connectivityManager.activeNetwork ?: return false
        val netAbility = connectivityManager.getNetworkCapabilities(currentActiveNet) ?: return false

        //연결 중이라면 true
        return when {
            netAbility.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            netAbility.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            else -> false
        }
    }
}