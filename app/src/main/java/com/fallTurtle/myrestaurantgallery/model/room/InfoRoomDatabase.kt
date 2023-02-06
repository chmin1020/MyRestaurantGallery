package com.fallTurtle.myrestaurantgallery.model.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [RestaurantInfo::class], version = 3)
abstract class InfoRoomDatabase: RoomDatabase() {
    abstract fun infoRoomDao(): InfoRoomDao

    companion object{
        private var instance: InfoRoomDatabase? = null

        @Synchronized
        fun getInstance(context: Context): InfoRoomDatabase{
            return instance ?: run{
                //null -> 최초 DB 접근. 새로운 DB 객체를 생성하여 적용
                Room.databaseBuilder(context.applicationContext, InfoRoomDatabase::class.java, "InfoDatabase")
                    .addMigrations(MIGRATION_1_2).addMigrations(MIGRATION_2_3)
                    .build()
                    .also { instance = it }
            }
        }
    }
}