package com.yilmaz.bimutfak.data.local.mapper

import com.yilmaz.bimutfak.data.local.entity.RecipeEntity
import com.yilmaz.bimutfak.domain.model.Recipe

fun Recipe.toRecipeEntity(
    cachedAt: Long
): RecipeEntity {
    return RecipeEntity(
        id = id,
        title = title,
        imageUrl = imageUrl,
        preparationTimeMinutes =
            preparationTimeMinutes,
        cookingTimeMinutes =
            cookingTimeMinutes,
        servings = servings,
        ingredients = ingredients,
        instructions = instructions,
        isFavorite = isFavorite,
        cachedAt = cachedAt
    )
}

fun RecipeEntity.toRecipe(): Recipe {
    return Recipe(
        id = id,
        title = title,
        imageUrl = imageUrl,
        preparationTimeMinutes =
            preparationTimeMinutes,
        cookingTimeMinutes =
            cookingTimeMinutes,
        servings = servings,
        ingredients = ingredients,
        instructions = instructions,
        isFavorite = isFavorite
    )
}