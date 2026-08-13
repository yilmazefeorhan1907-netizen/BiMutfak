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
import com.yilmaz.bimutfak.domain.repository.RecipeRepositoryContract
import com.yilmaz.bimutfak.data.local.dao.CuisineDao
import com.yilmaz.bimutfak.data.local.mapper.toCuisine
import com.yilmaz.bimutfak.data.local.mapper.toCuisineEntity
import com.yilmaz.bimutfak.domain.model.Cuisine

@Singleton
class RecipeRepository @Inject constructor(

    private val recipeApiService: RecipeApiService,
    private val recipeDao: RecipeDao,
    private val cuisineDao: CuisineDao
) : RecipeRepositoryContract {

    override suspend fun getCuisines(): List<Cuisine> {
        val cachedEntities = cuisineDao.getCuisines()
        val lastCacheTime = cuisineDao.getLastCacheTime()
        val currentTime = System.currentTimeMillis()

        val isCacheFresh =
            lastCacheTime != null &&
                    currentTime - lastCacheTime <
                    CUISINE_CACHE_DURATION_MILLIS

        if (
            cachedEntities.isNotEmpty() &&
            isCacheFresh
        ) {
            return cachedEntities.map { entity ->
                entity.toCuisine()
            }
        }

        return try {
            val cuisines = recipeApiService
                .getCuisines()
                .cuisines
                .orEmpty()
                .mapNotNull { dto ->
                    val name = dto.name
                        .orEmpty()
                        .trim()

                    if (name.isBlank()) {
                        null
                    } else {
                        Cuisine(
                            name = name,
                            country = dto.country
                                .orEmpty()
                                .trim()
                        )
                    }
                }
                .distinctBy { cuisine ->
                    cuisine.name
                }
                .sortedBy { cuisine ->
                    cuisine.name
                }

            if (cuisines.isNotEmpty()) {
                cuisineDao.replaceCuisines(
                    cuisines.map { cuisine ->
                        cuisine.toCuisineEntity(
                            cachedAt = currentTime
                        )
                    }
                )
            }

            cuisines
        } catch (exception: Exception) {
            if (cachedEntities.isNotEmpty()) {
                cachedEntities.map { entity ->
                    entity.toCuisine()
                }
            } else {
                throw exception
            }
        }
    }

    override suspend fun getRecipesByCuisine(
        cuisine: String
    ): List<Recipe> {

    val normalizedCuisine = cuisine.trim()

        if (normalizedCuisine.isBlank()) {
            return emptyList()
        }

        val cachedEntities =
            recipeDao.getRecipesByCuisine(
                cuisine = normalizedCuisine
            )

        val lastCacheTime =
            recipeDao.getLastCacheTimeForCuisine(
                cuisine = normalizedCuisine
            )

        val currentTime = System.currentTimeMillis()

        val isCacheFresh =
            lastCacheTime != null &&
                    currentTime - lastCacheTime <
                    RECIPE_CACHE_DURATION_MILLIS

        if (
            cachedEntities.isNotEmpty() &&
            isCacheFresh
        ) {
            return cachedEntities.map { entity ->
                entity.toRecipe()
            }
        }

        return try {
            val cachedDetailsById = cachedEntities
                .filter { entity ->
                    entity.hasDetails
                }
                .associateBy { entity ->
                    entity.id
                }

            val recipes = recipeApiService
                .getRecipesByCuisine(
                    cuisine = normalizedCuisine
                )
                .meals
                .orEmpty()
                .map { meal ->
                    val summaryRecipe = meal.toRecipe(
                        fallbackCuisine =
                            normalizedCuisine
                    )

                    cachedDetailsById[
                        summaryRecipe.id
                    ]?.toRecipe() ?: summaryRecipe
                }
                .sortedBy { recipe ->
                    recipe.title
                }

            recipeDao.replaceRecipesForCuisine(
                cuisine = normalizedCuisine,
                recipes = recipes.map { recipe ->
                    recipe.toRecipeEntity(
                        cachedAt = currentTime
                    )
                }
            )

            recipes
        } catch (exception: Exception) {
            if (cachedEntities.isNotEmpty()) {
                cachedEntities.map { entity ->
                    entity.toRecipe()
                }
            } else {
                throw exception
            }
        }
    }

    override suspend fun searchRecipes(
        query: String
    ): List<Recipe> {
        val normalizedQuery = query.trim()

        if (normalizedQuery.length < 3) {
            return emptyList()
        }

        val cachedEntities =
            recipeDao.searchRecipes(
                query = normalizedQuery
            )

        return try {
            val currentTime =
                System.currentTimeMillis()

            val recipes = recipeApiService
                .searchRecipes(
                    query = normalizedQuery
                )
                .meals
                .orEmpty()
                .map { meal ->
                    meal.toRecipe()
                }
                .sortedBy { recipe ->
                    recipe.title
                }

            if (recipes.isNotEmpty()) {
                recipeDao.insertRecipes(
                    recipes.map { recipe ->
                        recipe.toRecipeEntity(
                            cachedAt = currentTime
                        )
                    }
                )
            }

            recipes
        } catch (exception: Exception) {
            if (cachedEntities.isNotEmpty()) {
                cachedEntities.map { entity ->
                    entity.toRecipe()
                }
            } else {
                throw exception
            }
        }
    }

    override suspend fun getRecipeById(
        recipeId: String
    ): Recipe? {
        val normalizedId = recipeId.trim()

        if (normalizedId.isBlank()) {
            return null
        }

        val cachedEntity =
            recipeDao.getRecipeById(normalizedId)

        if (cachedEntity?.hasDetails == true) {
            return cachedEntity.toRecipe()
        }

        return try {
            val remoteRecipe = recipeApiService
                .getRecipeById(
                    recipeId = normalizedId
                )
                .meals
                .orEmpty()
                .firstOrNull()
                ?.toRecipe(
                    fallbackCuisine =
                        cachedEntity?.cuisine.orEmpty()
                )

            if (remoteRecipe != null) {
                recipeDao.insertRecipe(
                    remoteRecipe.toRecipeEntity(
                        cachedAt =
                            System.currentTimeMillis()
                    )
                )
            }

            remoteRecipe ?: cachedEntity?.toRecipe()
        } catch (exception: Exception) {
            cachedEntity?.toRecipe()
                ?: throw exception
        }
    }

    private fun MealDto.toRecipe(
        fallbackCuisine: String = ""
    ): Recipe {
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
            cuisine = area
                .orEmpty()
                .ifBlank { fallbackCuisine },
            preparationTimeMinutes = 0,
            cookingTimeMinutes = 0,
            servings = 1,
            ingredients = recipeIngredients,
            instructions = instructionSteps,
            isFavorite = false
        )
    }

    companion object {

        private const val RECIPE_CACHE_DURATION_MILLIS =
            6 * 60 * 60 * 1000L

        private const val CUISINE_CACHE_DURATION_MILLIS =
            24 * 60 * 60 * 1000L
    }
}