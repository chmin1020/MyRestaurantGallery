package com.fallTurtle.myrestaurantgallery.response

data class SearchPoiInfo(
    val totalCount: String,
    val count: String,
    val page: String,
    val pois: Pois
)