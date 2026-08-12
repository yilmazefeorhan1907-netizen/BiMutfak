package com.yilmaz.bimutfak.data.repository

import com.yilmaz.bimutfak.data.local.dao.RecipeDao
import com.yilmaz.bimutfak.data.local.mapper.toRecipe
import com.yilmaz.bimutfak.data.local.mapper.toRecipeEntity
import com.yilmaz.bimutfak.data.remote.api.RecipeApiService
import com.yilmaz.bimutfak.data.remote.dto.MealDto
import com.yilmaz.bimutfak.domain.model.Recipe
import com.yilmaz.bimutfak.domain.model.RecipeIngredient
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RecipeRepository @Inject constructor(
    private val recipeApiService: RecipeApiService,
    private val recipeDao: RecipeDao
) {

    suspend fun getRecipes(
        limit: Int = DEMO_RECIPE_LIMIT
    ): List<Recipe> {
        val cachedEntities = recipeDao.getRecipes()
        val lastCacheTime = recipeDao.getLastCacheTime()
        val currentTime = System.currentTimeMillis()

        val isCacheFresh =
            lastCacheTime != null &&
                    currentTime - lastCacheTime <
                    RECIPE_CACHE_DURATION_MILLIS

        if (
            cachedEntities.isNotEmpty() &&
            isCacheFresh
        ) {
            return cachedEntities
                .take(limit)
                .map { entity ->
                    entity.toRecipe()
                }
        }

        return try {
            val response =
                recipeApiService.getRecipesByFirstLetter(
                    firstLetter = DEFAULT_FIRST_LETTER
                )

            val recipes = response.meals
                .orEmpty()
                .take(limit)
                .map { meal ->
                    meal.toRecipe()
                }

            if (recipes.isNotEmpty()) {
                val entities = recipes.map { recipe ->
                    recipe.toRecipeEntity(
                        cachedAt = currentTime
                    )
                }

                recipeDao.replaceRecipes(entities)
            }

            recipes
        } catch (exception: Exception) {
            if (cachedEntities.isNotEmpty()) {
                cachedEntities
                    .take(limit)
                    .map { entity ->
                        entity.toRecipe()
                    }
            } else {
                throw exception
            }
        }
    }

    private fun MealDto.toRecipe(): Recipe {
        val instructionSteps = instructions
            .orEmpty()
            .split(Regex("\\r?\\n"))
            .map { step ->
                step.trim()
            }
            .filter { step ->
                step.isNotBlank()
            }

        val ingredientNames = listOf(
            ingredient1,
            ingredient2,
            ingredient3,
            ingredient4,
            ingredient5,
            ingredient6,
            ingredient7,
            ingredient8,
            ingredient9,
            ingredient10,
            ingredient11,
            ingredient12,
            ingredient13,
            ingredient14,
            ingredient15,
            ingredient16,
            ingredient17,
            ingredient18,
            ingredient19,
            ingredient20
        )

        val ingredientMeasures = listOf(
            measure1,
            measure2,
            measure3,
            measure4,
            measure5,
            measure6,
            measure7,
            measure8,
            measure9,
            measure10,
            measure11,
            measure12,
            measure13,
            measure14,
            measure15,
            measure16,
            measure17,
            measure18,
            measure19,
            measure20
        )

        val recipeIngredients = ingredientNames
            .zip(ingredientMeasures)
            .mapNotNull { (name, measure) ->
                val cleanName =
                    name.orEmpty().trim()

                val cleanMeasure =
                    measure.orEmpty().trim()

                if (cleanName.isBlank()) {
                    null
                } else {
                    RecipeIngredient(
                        name = cleanName,
                        measure = cleanMeasure
                    )
                }
            }

        return Recipe(
            id = id.orEmpty(),
            title = title.orEmpty(),
            imageUrl = imageUrl.orEmpty(),
            preparationTimeMinutes = 0,
            cookingTimeMinutes = 0,
            servings = 1,
            ingredients = recipeIngredients,
            instructions = instructionSteps,
            isFavorite = false
        )
    }

    companion object {
        const val DEMO_RECIPE_LIMIT = 5

        private const val DEFAULT_FIRST_LETTER = "c"

        private const val RECIPE_CACHE_DURATION_MILLIS =
            6 * 60 * 60 * 1000L
    }
}