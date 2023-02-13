package com.fallTurtle.myrestaurantgallery.model.room

import androidx.room.*

@Dao
interface InfoRoomDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: RestaurantInfo)

    @Update
    suspend fun update(item: RestaurantInfo)

    @Delete
    suspend fun delete(item: RestaurantInfo)

    @Query("SELECT * FROM RestaurantInfo ORDER BY dbID")
    suspend fun getAllItems(): List<RestaurantInfo>

    @Query("DELETE FROM RestaurantInfo")
    suspend fun clearAllItems()

    @Query("SELECT * FROM RestaurantInfo WHERE dbID is :id")
    suspend fun getProperItem(id: String): RestaurantInfo?
}