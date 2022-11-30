package com.fallTurtle.myrestaurantgallery.model.retrofit.response

data class PlaceMeta(
    var total_count: Int,               // 검색어에 검색된 문서 수
    var pageable_count: Int,            // total_count 중 노출 가능 문서 수, 최대 45 (API에서 최대 45개 정보만 제공)
    var is_end: Boolean,                // 현재 페이지가 마지막 페이지인지 여부, 값이 false면 page를 증가시켜 다음 페이지를 요청할 수 있음
)