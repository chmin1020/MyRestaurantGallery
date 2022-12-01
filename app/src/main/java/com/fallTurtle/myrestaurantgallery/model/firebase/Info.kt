package com.fallTurtle.myrestaurantgallery.model.firebase

import java.io.Serializable

data class Info(
        val dbID:String = "",
        var imgUsed:Boolean = false,
        var image:String = "",
        var name:String = "",
        var location:String = "",
        var categoryNum:Int = 0,
        var category:String = "",
        var rate:Int = 0,
        var memo:String = "",
        var date:String = "",
        var latitude:Double = -1.0,
        var longitude:Double = -1.0): Serializable
