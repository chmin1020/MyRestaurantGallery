package com.fallTurtle.myrestaurantgallery.model.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.fallTurtle.myrestaurantgallery.etc.DEFAULT_LOCATION
import com.fallTurtle.myrestaurantgallery.etc.ID_PATTERN
import java.text.SimpleDateFormat
import java.util.*

@Entity
data class RestaurantInfo(
    @PrimaryKey
    val dbID: String
        = SimpleDateFormat(ID_PATTERN, Locale.KOREA).format(Date(System.currentTimeMillis())).toString(),

    var imageName: String? = null,
    var imagePath: String? = null,
    var name: String = "",
    var categoryNum: Int = 0,
    var category: String = "",
    var rate: Int = 0,
    var memo: String = "",
    var date: String = "",
    var latitude: Double = DEFAULT_LOCATION,
    var longitude: Double = DEFAULT_LOCATION
)
