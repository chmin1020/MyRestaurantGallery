package com.fallTurtle.myrestaurantgallery.item

/**
 * 각 식당에 대한 정보 중 리스트에 필요한 것을 담는 데이터 클래스.
 * 각각 전체 주소, 이름, 카테고리(한식, 중식, ...), 위치 정보(위도, 경도)
 */
data class LocationResult(
    val fullAddress:String,
    val name:String,
    val category: String,
    val lp:LocationPair)
