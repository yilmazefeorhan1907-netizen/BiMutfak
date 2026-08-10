package com.yilmaz.bimutfak.data.repository

import com.yilmaz.bimutfak.data.remote.api.RecipeApiService
import com.yilmaz.bimutfak.data.remote.dto.MealDto
import com.yilmaz.bimutfak.domain.model.Recipe
import javax.inject.Inject
import javax.inject.Singleton

// API tariflerini uygulamanın kullanabileceği Recipe modeline dönüştürür.
@Singleton
class RecipeRepository @Inject constructor(
    private val recipeApiService: RecipeApiService
) {

    suspend fun getRecipes(
        limit: Int = DEMO_RECIPE_LIMIT
    ): List<Recipe> {
        val response =
            recipeApiService.getRecipesByFirstLetter(
                firstLetter = DEFAULT_FIRST_LETTER
            )

        return response.meals
            .orEmpty()
            .take(limit)
            .map { meal ->
                meal.toRecipe()
            }
    }

    private fun MealDto.toRecipe(): Recipe {
        val instructionSteps = instructions
            .orEmpty()
            .split(
                Regex("\\r?\\n")
            )
            .map { step ->
                step.trim()
            }
            .filter { step ->
                step.isNotBlank()
            }

        return Recipe(
            id = id.orEmpty(),
            title = title.orEmpty(),
            imageUrl = imageUrl.orEmpty(),

            // TheMealDB bu süreleri doğrudan vermediği için
            // şimdilik varsayılan değer kullanmıyoruz.
            preparationTimeMinutes = 0,
            cookingTimeMinutes = 0,
            servings = 1,

            // Malzemeleri tarif detayını oluştururken ekleyeceğiz.
            ingredients = emptyList(),
            instructions = instructionSteps,
            isFavorite = false
        )
    }

    companion object {
        const val DEMO_RECIPE_LIMIT = 5

        private const val DEFAULT_FIRST_LETTER = "c"
    }
}