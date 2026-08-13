package com.yilmaz.bimutfak.domain.usecase.recipe

import com.yilmaz.bimutfak.domain.model.Recipe
import com.yilmaz.bimutfak.domain.model.RecipeSelectionResult
import com.yilmaz.bimutfak.domain.repository.RecipeRepositoryContract
import com.yilmaz.bimutfak.domain.repository.RecipeSelectionRepositoryContract
import javax.inject.Inject
import com.yilmaz.bimutfak.domain.model.Cuisine

class GetCuisinesUseCase @Inject constructor(
    private val repository: RecipeRepositoryContract
) {

    suspend operator fun invoke(): List<Cuisine> {
        return repository.getCuisines()
    }
}

class GetRecipesByCuisineUseCase @Inject constructor(
    private val repository: RecipeRepositoryContract
) {

    suspend operator fun invoke(
        cuisine: String
    ): List<Recipe> {
        return repository.getRecipesByCuisine(cuisine)
    }
}

class SearchRecipesUseCase @Inject constructor(
    private val repository:
    RecipeRepositoryContract
) {

    suspend operator fun invoke(
        query: String
    ): List<Recipe> {
        return repository.searchRecipes(query)
    }
}

class GetRecipeByIdUseCase @Inject constructor(
    private val repository: RecipeRepositoryContract
) {

    suspend operator fun invoke(
        recipeId: String
    ): Recipe? {
        return repository.getRecipeById(recipeId)
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