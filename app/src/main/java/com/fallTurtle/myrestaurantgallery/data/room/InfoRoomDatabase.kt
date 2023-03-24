package com.fallTurtle.myrestaurantgallery.data.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [RestaurantInfo::class], version = 4)
abstract class InfoRoomDatabase : RoomDatabase() {
    abstract fun infoRoomDao(): InfoRoomDao
}