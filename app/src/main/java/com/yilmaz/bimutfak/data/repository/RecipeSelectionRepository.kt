package com.yilmaz.bimutfak.data.repository

import com.yilmaz.bimutfak.data.firestore.FirestoreRecipeSelectionDataSource
import com.yilmaz.bimutfak.domain.model.Recipe
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RecipeSelectionRepository @Inject constructor(
    private val authRepository: AuthRepository,
    private val dataSource: FirestoreRecipeSelectionDataSource
) {

    suspend fun getFavoriteRecipes(): List<Recipe> {
        return dataSource.getFavoriteRecipes(
            userId = requireCurrentUserId()
        )
    }

    suspend fun getDailyMenu(): List<Recipe> {
        return dataSource.getDailyMenu(
            userId = requireCurrentUserId()
        )
    }

    suspend fun toggleFavorite(
        recipe: Recipe
    ): RecipeSelectionResult {
        val userId = requireCurrentUserId()
        val currentFavorites =
            dataSource.getFavoriteRecipes(userId)

        val isAlreadyFavorite =
            currentFavorites.any { favorite ->
                favorite.id == recipe.id
            }

        if (isAlreadyFavorite) {
            dataSource.deleteFavoriteRecipe(
                userId = userId,
                recipeId = recipe.id
            )

            return RecipeSelectionResult.REMOVED
        }

        if (
            currentFavorites.size >=
            MAXIMUM_FAVORITE_RECIPE_COUNT
        ) {
            return RecipeSelectionResult.FAVORITE_LIMIT_REACHED
        }

        dataSource.saveFavoriteRecipe(
            userId = userId,
            recipe = recipe
        )

        return RecipeSelectionResult.ADDED
    }

    suspend fun toggleDailyMenu(
        recipe: Recipe
    ): RecipeSelectionResult {
        val userId = requireCurrentUserId()
        val currentDailyMenu =
            dataSource.getDailyMenu(userId)

        val isAlreadyInDailyMenu =
            currentDailyMenu.any { menuRecipe ->
                menuRecipe.id == recipe.id
            }

        if (isAlreadyInDailyMenu) {
            dataSource.deleteDailyMenuRecipe(
                userId = userId,
                recipeId = recipe.id
            )

            return RecipeSelectionResult.REMOVED
        }

        if (
            currentDailyMenu.size >=
            MAXIMUM_DAILY_MENU_RECIPE_COUNT
        ) {
            return RecipeSelectionResult.DAILY_MENU_LIMIT_REACHED
        }

        dataSource.saveDailyMenuRecipe(
            userId = userId,
            recipe = recipe
        )

        return RecipeSelectionResult.ADDED
    }

    private fun requireCurrentUserId(): String {
        return authRepository.currentUserId
            ?: error(
                "Tarif seçimi için kullanıcı oturumu gerekli."
            )
    }

    companion object {
        const val MAXIMUM_FAVORITE_RECIPE_COUNT = 5
        const val MAXIMUM_DAILY_MENU_RECIPE_COUNT = 3
    }
}

enum class RecipeSelectionResult {
    ADDED,
    REMOVED,
    FAVORITE_LIMIT_REACHED,
    DAILY_MENU_LIMIT_REACHED
}