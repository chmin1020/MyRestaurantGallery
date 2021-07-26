package com.fallTurtle.myrestaurantgallery.item

import android.graphics.Bitmap

class Piece{
    private var image:Bitmap? = null
    private var name:String? = null
    private var location:String? = null
    private var genre:String? = null
    private var rate:Float? = null

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

    fun getRate() :Float?{
        return rate
    }



}