package com.yilmaz.bimutfak.domain.repository

import com.yilmaz.bimutfak.domain.model.Recipe
import com.yilmaz.bimutfak.domain.model.RecipeSelectionResult

interface RecipeSelectionRepositoryContract {

    suspend fun getFavoriteRecipes(): List<Recipe>

    suspend fun getDailyMenu(): List<Recipe>

    suspend fun toggleFavorite(
        recipe: Recipe
    ): RecipeSelectionResult

    suspend fun toggleDailyMenu(
        recipe: Recipe
    ): RecipeSelectionResult
}