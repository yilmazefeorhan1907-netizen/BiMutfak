package com.yilmaz.bimutfak.domain.usecase.recipe

import com.yilmaz.bimutfak.domain.model.Recipe
import com.yilmaz.bimutfak.domain.model.RecipeSelectionResult
import com.yilmaz.bimutfak.domain.repository.RecipeRepositoryContract
import com.yilmaz.bimutfak.domain.repository.RecipeSelectionRepositoryContract
import javax.inject.Inject

class GetRecipesUseCase @Inject constructor(
    private val repository: RecipeRepositoryContract
) {

    suspend operator fun invoke(): List<Recipe> {
        return repository.getRecipes()
    }
}

class GetFavoriteRecipesUseCase @Inject constructor(
    private val repository: RecipeSelectionRepositoryContract
) {

    suspend operator fun invoke(): List<Recipe> {
        return repository.getFavoriteRecipes()
    }
}

class GetDailyMenuUseCase @Inject constructor(
    private val repository: RecipeSelectionRepositoryContract
) {

    suspend operator fun invoke(): List<Recipe> {
        return repository.getDailyMenu()
    }
}

class ToggleFavoriteUseCase @Inject constructor(
    private val repository: RecipeSelectionRepositoryContract
) {

    suspend operator fun invoke(
        recipe: Recipe
    ): RecipeSelectionResult {
        return repository.toggleFavorite(recipe)
    }
}

class ToggleDailyMenuUseCase @Inject constructor(
    private val repository: RecipeSelectionRepositoryContract
) {

    suspend operator fun invoke(
        recipe: Recipe
    ): RecipeSelectionResult {
        return repository.toggleDailyMenu(recipe)
    }
}