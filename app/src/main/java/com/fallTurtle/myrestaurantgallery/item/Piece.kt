package com.fallTurtle.myrestaurantgallery.item

import android.graphics.Bitmap

class Piece{
    private var imgUsed:Boolean = false
    private var image:Bitmap? = null
    private var name:String? = null
    private var location:String? = null
    private var genre:String? = null
    private var rate:Int? = null

    fun setImgUsed(tf: Boolean){
        imgUsed = tf
    }

    fun getImgUsed() :Boolean{
        return imgUsed
    }

    fun getImage() :Bitmap?{
        return image
    }

    fun getName() :String?{
        return name
    }

    fun getLocation() :String?{
        return location
    }

    fun getGenre() :String?{
        return genre
    }

    fun getRate() :Int?{
        return rate
    }
}