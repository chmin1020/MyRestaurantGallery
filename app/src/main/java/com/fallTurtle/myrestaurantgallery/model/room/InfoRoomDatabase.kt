package com.fallTurtle.myrestaurantgallery.model.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Info::class], version = 1)
abstract class InfoRoomDatabase: RoomDatabase() {
    abstract fun infoRoomDao(): InfoRoomDao

    companion object{
        private var instance: InfoRoomDatabase? = null

        @Synchronized
        fun getInstance(context: Context): InfoRoomDatabase?{
            if(instance == null) {
                instance = Room.databaseBuilder(
                    context.applicationContext,
                    InfoRoomDatabase::class.java,
                    "InfoDatabase").build()
            }
            return instance
        }
    }

}