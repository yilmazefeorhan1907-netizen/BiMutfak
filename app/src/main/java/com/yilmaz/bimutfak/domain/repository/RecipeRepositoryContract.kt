package com.yilmaz.bimutfak.domain.repository

import com.yilmaz.bimutfak.domain.model.Cuisine
import com.yilmaz.bimutfak.domain.model.Recipe

interface RecipeRepositoryContract {

    suspend fun getCuisines(): List<Cuisine>

    suspend fun getRecipesByCuisine(
        cuisine: String
    ): List<Recipe>

    suspend fun searchRecipes(
        query: String
    ): List<Recipe>

    suspend fun getRecipeById(
        recipeId: String
    ): Recipe?
}