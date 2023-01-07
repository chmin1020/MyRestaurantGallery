package com.fallTurtle.myrestaurantgallery.model.room

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface InfoRoomDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(item: Info)

    @Delete
    fun delete(item: Info)

    @Query("SELECT * FROM Info")
    fun getAllItems(): LiveData<List<Info>>

    @Query("DELETE FROM Info")
    fun clearAllItems()
}