package com.yilmaz.bimutfak.domain.model

// Uygulamada gösterilecek tarif bilgisini temsil eder.
data class Recipe(
    val id: String = "",
    val title: String = "",
    val imageUrl: String = "",
    val preparationTimeMinutes: Int = 0,
    val cookingTimeMinutes: Int = 0,
    val servings: Int = 1,
    val ingredients: List<RecipeIngredient> = emptyList(),
    val instructions: List<String> = emptyList(),
    val isFavorite: Boolean = false
)