package com.yilmaz.bimutfak.ui.recipe

import androidx.annotation.StringRes
import com.yilmaz.bimutfak.domain.model.Recipe

// Bi’Tarif ekranının güncel arayüz durumunu tutar.
data class RecipeUiState(
    val recipes: List<Recipe> = emptyList(),
    val selectedRecipe: Recipe? = null,

    val favoriteRecipeIds: Set<String> = emptySet(),
    val dailyMenuRecipeIds: Set<String> = emptySet(),
    val processingRecipeId: String? = null,

    val isLoading: Boolean = true,

    @StringRes val errorMessageResId: Int? = null,
    @StringRes val userMessageResId: Int? = null
)