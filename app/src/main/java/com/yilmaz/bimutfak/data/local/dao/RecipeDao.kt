package com.yilmaz.bimutfak.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.yilmaz.bimutfak.data.local.entity.RecipeEntity

@Dao
interface RecipeDao {

    @Query("SELECT * FROM recipes ORDER BY title ASC")
    suspend fun getRecipes(): List<RecipeEntity>

    @Query("SELECT MAX(cachedAt) FROM recipes")
    suspend fun getLastCacheTime(): Long?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipes(
        recipes: List<RecipeEntity>
    )

    @Query("DELETE FROM recipes")
    suspend fun clearRecipes()

    @Transaction
    suspend fun replaceRecipes(
        recipes: List<RecipeEntity>
    ) {
        clearRecipes()
        insertRecipes(recipes)
    }
}