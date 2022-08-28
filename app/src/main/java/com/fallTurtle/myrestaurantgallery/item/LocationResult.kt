package com.fallTurtle.myrestaurantgallery.item

class LocationResult(
    private var fullAddr:String? = null,
    private var name:String? = null,
    private var category: String? = null,
    private var lp:LocationPair? = null){

    fun getFullAddr() :String?{
        return fullAddr
    }

    fun getName() :String?{
        return name
    }

    fun getCategory() :String?{
        return category
    }

    fun getLp() :LocationPair?{
        return lp
    }

}


