package com.fallTurtle.myrestaurantgallery.model.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class Info(
        @PrimaryKey val dbID:String = "",
        var image:String? = null,
        var name:String = "",
        var location:String = "",
        var categoryNum:Int = 0,
        var category:String = "",
        var rate:Int = 0,
        var memo:String = "",
        var date:String = "",
        var latitude:Double = -1.0,
        var longitude:Double = -1.0): Serializable