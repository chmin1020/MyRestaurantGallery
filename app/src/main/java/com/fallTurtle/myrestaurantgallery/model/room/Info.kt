package com.fallTurtle.myrestaurantgallery.model.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.SimpleDateFormat
import java.util.*

@Entity
data class Info(
        @PrimaryKey val dbID:String = "",
        var imageName:String? = null,
        var imagePath:String? = null,
        var name:String = "",
        var location:String = "",
        var categoryNum:Int = 0,
        var category:String = "",
        var rate:Int = 0,
        var memo:String = "",
        var date:String = SimpleDateFormat ( "yyyy년 M월 d일", Locale.KOREA).format(Date(Calendar.getInstance().timeInMillis)),
        var latitude:Double = -1.0,
        var longitude:Double = -1.0)
