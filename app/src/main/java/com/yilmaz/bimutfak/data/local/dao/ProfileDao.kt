package com.yilmaz.bimutfak.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.yilmaz.bimutfak.data.local.entity.ProfileEntity

@Dao
interface ProfileDao {

    @Query(
        "SELECT * FROM profiles " +
                "WHERE uid = :userId LIMIT 1"
    )
    suspend fun getProfile(
        userId: String
    ): ProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveProfile(
        profile: ProfileEntity
    )
}