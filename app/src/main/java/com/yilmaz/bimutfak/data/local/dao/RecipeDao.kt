package com.yilmaz.bimutfak.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.yilmaz.bimutfak.data.local.entity.RecipeEntity

@Dao
interface RecipeDao {

    @Query(
        "SELECT * FROM recipes " +
                "WHERE cuisine = :cuisine " +
                "ORDER BY title ASC"
    )
    suspend fun getRecipesByCuisine(
        cuisine: String
    ): List<RecipeEntity>

    @Query(
        "SELECT MIN(cachedAt) FROM recipes " +
                "WHERE cuisine = :cuisine"
    )
    suspend fun getLastCacheTimeForCuisine(
        cuisine: String
    ): Long?

    @Query(
        "SELECT * FROM recipes " +
                "WHERE id = :recipeId LIMIT 1"
    )
    suspend fun getRecipeById(
        recipeId: String
    ): RecipeEntity?

    @Query(
        """
    SELECT * FROM recipes
    WHERE LOWER(title) LIKE
        '%' || LOWER(:query) || '%'
    ORDER BY title ASC
    """
    )
    suspend fun searchRecipes(
        query: String
    ): List<RecipeEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipe(
        recipe: RecipeEntity
    )

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipes(
        recipes: List<RecipeEntity>
    )

    @Query(
        "DELETE FROM recipes " +
                "WHERE cuisine = :cuisine"
    )
    suspend fun clearRecipesForCuisine(
        cuisine: String
    )

    @Transaction
    suspend fun replaceRecipesForCuisine(
        cuisine: String,
        recipes: List<RecipeEntity>
    ) {
        clearRecipesForCuisine(cuisine)
        insertRecipes(recipes)
    }
}