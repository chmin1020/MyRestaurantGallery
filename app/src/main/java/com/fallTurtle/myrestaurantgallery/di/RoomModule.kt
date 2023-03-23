package com.fallTurtle.myrestaurantgallery.di

import android.content.Context
import androidx.room.Room
import com.fallTurtle.myrestaurantgallery.data.room.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RoomModule {
    @Provides
    @Singleton
    fun provideRoomDatabase(@ApplicationContext context: Context): InfoRoomDatabase {
        return Room.databaseBuilder(context.applicationContext, InfoRoomDatabase::class.java, "InfoDatabase")
                    .addMigrations(MIGRATION_1_2).addMigrations(MIGRATION_2_3).addMigrations(MIGRATION_3_4)
                    .build()
    }

    @Provides
    fun provideBigListDao(roomDB: InfoRoomDatabase): InfoRoomDao {
        return roomDB.infoRoomDao()
    }
}