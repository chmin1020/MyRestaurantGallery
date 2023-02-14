package com.fallTurtle.myrestaurantgallery.model.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class RestaurantInfo(
        @PrimaryKey val dbID:String = "",
        var imageName:String?,
        var imagePath:String?,
        var name:String,
        var location:String,
        var categoryNum:Int,
        var category:String,
        var rate:Int,
        var memo:String,
        var date:String,
        var latitude:Double,
        var longitude:Double)
