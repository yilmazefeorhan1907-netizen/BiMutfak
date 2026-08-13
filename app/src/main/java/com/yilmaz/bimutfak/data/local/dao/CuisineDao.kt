package com.yilmaz.bimutfak.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.yilmaz.bimutfak.data.local.entity.CuisineEntity

@Dao
interface CuisineDao {

    @Query("SELECT * FROM cuisines ORDER BY name ASC")
    suspend fun getCuisines(): List<CuisineEntity>

    @Query("SELECT MAX(cachedAt) FROM cuisines")
    suspend fun getLastCacheTime(): Long?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCuisines(
        cuisines: List<CuisineEntity>
    )

    @Query("DELETE FROM cuisines")
    suspend fun clearCuisines()

    @Transaction
    suspend fun replaceCuisines(
        cuisines: List<CuisineEntity>
    ) {
        clearCuisines()
        insertCuisines(cuisines)
    }
}