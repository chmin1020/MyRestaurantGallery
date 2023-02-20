package com.fallTurtle.myrestaurantgallery.model.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.fallTurtle.myrestaurantgallery.etc.DEFAULT_LOCATION

@Entity
data class RestaurantInfo(
    @PrimaryKey val dbID: String = "",
    var imageName: String? = "",
    var imagePath: String? = "",
    var name: String = "",
    var categoryNum: Int = 0,
    var category: String = "",
    var rate: Int = 0,
    var memo: String = "",
    var date: String = "",
    var latitude: Double = DEFAULT_LOCATION,
    var longitude: Double = DEFAULT_LOCATION
)
