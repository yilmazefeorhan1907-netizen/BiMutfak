package com.yilmaz.bimutfak.ui.recipe

import androidx.annotation.StringRes
import com.yilmaz.bimutfak.domain.model.Recipe

// Bi’Tarif ekranının güncel arayüz durumunu tutar.
data class RecipeUiState(
    val recipes: List<Recipe> = emptyList(),
    val selectedRecipe: Recipe? = null,
    val isLoading: Boolean = true,

    @StringRes val errorMessageResId: Int? = null
)