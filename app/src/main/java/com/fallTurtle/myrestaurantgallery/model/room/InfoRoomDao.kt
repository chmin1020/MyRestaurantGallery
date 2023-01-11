package com.fallTurtle.myrestaurantgallery.model.room

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface InfoRoomDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: Info)

    @Delete
    suspend fun delete(item: Info)

    @Query("SELECT * FROM Info")
    fun getAllItems(): LiveData<List<Info>>

    @Query("DELETE FROM Info")
    suspend fun clearAllItems()

    @Query("SELECT * FROM Info WHERE dbID is :id")
    suspend fun getProperItem(id: String): Info
}