package com.fallTurtle.myrestaurantgallery.retrofit.response

data class Place(
    var id: String,                     // 장소 ID
    var place_name: String,             // 장소명, 업체명
    var category_name: String,          // 카테고리 이름
    var category_group_code: String,    // 중요 카테고리만 그룹핑한 카테고리 그룹 코드
    var category_group_name: String,    // 중요 카테고리만 그룹핑한 카테고리 그룹명
    var address_name: String,           // 전체 지번 주소
    var road_address_name: String,      // 전체 도로명 주소
    var x: String,                      // X 좌표값 혹은 longitude
    var y: String,                      // Y 좌표값 혹은 latitude
    var place_url: String,              // 장소 상세페이지 URL
)