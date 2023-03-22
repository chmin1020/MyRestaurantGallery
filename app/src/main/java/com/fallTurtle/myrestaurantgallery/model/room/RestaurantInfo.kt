package com.fallTurtle.myrestaurantgallery.model.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.fallTurtle.myrestaurantgallery.etc.DATE_PATTERN
import com.fallTurtle.myrestaurantgallery.etc.UNDECIDED_LOCATION
import com.fallTurtle.myrestaurantgallery.etc.ID_PATTERN
import java.text.SimpleDateFormat
import java.util.*

@Entity
data class RestaurantInfo(
    @PrimaryKey
    val dbID: String
        = SimpleDateFormat(ID_PATTERN, Locale.KOREA).format(Date(System.currentTimeMillis())),

    var imageName: String? = null,
    var imagePath: String? = null,
    var name: String = "",
    var categoryNum: Int = 0,
    var category: String = "",
    var rate: Int = 0,
    var memo: String = "",
    var date: String = SimpleDateFormat(DATE_PATTERN, Locale.KOREA).format(Date(System.currentTimeMillis())),
    var latitude: Double = UNDECIDED_LOCATION,
    var longitude: Double = UNDECIDED_LOCATION
)
