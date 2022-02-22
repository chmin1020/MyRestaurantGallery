package com.fallTurtle.myrestaurantgallery.item

class LocationResult(
    private var fullAddr:String? = null,
    private var name:String? = null,
    private var lp:LocationPair? = null){

    fun setFullAddr(addr: String){
        fullAddr = addr
    }

    fun getFullAddr() :String?{
        return fullAddr
    }

    fun setName(nm : String){
        name = nm
    }

    fun getName() :String?{
        return name
    }

    fun setLp(x: Float, y: Float){
        lp = LocationPair(x,y)
    }

    fun getLp() :LocationPair?{
        return lp
    }

}


