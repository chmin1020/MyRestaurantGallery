package com.fallTurtle.myrestaurantgallery.response

/**
 * Retrofit2 API를 통해서 받아올 정보를 담는 클래스.
 * meta, documents 파트로 나뉘며 각각 검색 메타데이터와 실제 각 장소의 정보 리스트를 의미한다.
 */
data class LocationResponse(
    var meta: PlaceMeta, //검색 메타데이터
    var documents:List<Place> //각 장소들의 리스트
)

