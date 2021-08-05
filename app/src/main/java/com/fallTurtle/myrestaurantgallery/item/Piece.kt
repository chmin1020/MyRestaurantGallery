package com.fallTurtle.myrestaurantgallery.item

import android.graphics.Bitmap

class Piece{
    private var dbID:String? = null
    private var imgUsed:Boolean = false
    private var image:Bitmap? = null
    private var name:String? = null
    private var location:String? = null
    private var genreNum:Int? = null
    private var genre:String? = null
    private var rate:Int? = null
    private var date:String? = null
    private var memo:String? = null

    fun setDBID(id: String){
        dbID = id
    }

    fun getDBID() :String?{
        return dbID
    }

    fun setImgUsed(tf: Boolean){
        imgUsed = tf
    }

    fun getImgUsed() :Boolean{
        return imgUsed
    }

    fun setImage(tf: Boolean){
        imgUsed = tf
    }

    fun getImage() :Bitmap?{
        return image
    }

    fun setName(nm : String?){
        name = nm
    }

    fun getName() :String?{
        return name
    }

    fun setLocation(lc : String?) {
        location = lc
    }

    fun getLocation() :String?{
        return location
    }

    fun setGenreNum(gn : Int?){
        genreNum = gn
    }

    fun getGenreNum() :Int?{
        return genreNum
    }

    fun setGenre(gr : String?){
        genre = gr
    }

    fun getGenre() :String?{
        return genre
    }

    fun setRate(rt : Int?){
        rate = rt
    }

    fun getRate() :Int?{
        return rate
    }

    fun setDate(dt : String?){
        date = dt
    }

    fun getDate() :String?{
        return date
    }

    fun setMemo(mm : String?){
        memo = mm
    }

    fun getMemo() :String?{
        return memo
    }
}