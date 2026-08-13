package com.yilmaz.bimutfak.ui.recipe

import androidx.annotation.StringRes
import com.yilmaz.bimutfak.domain.model.Recipe
import com.yilmaz.bimutfak.domain.model.Cuisine

// Bi’Tarif ekranının güncel arayüz durumunu tutar.
data class RecipeUiState(
    val recipes: List<Recipe> = emptyList(),
    val cuisines: List<Cuisine> = emptyList(),

    val currentPage: Int = 0,
    val searchQuery: String = "",
    val isSearching: Boolean = false,
    val selectedCuisine: String = "",

    val selectedRecipe: Recipe? = null,

    val favoriteRecipeIds: Set<String> = emptySet(),
    val dailyMenuRecipeIds: Set<String> = emptySet(),
    val processingRecipeId: String? = null,

    val isLoading: Boolean = true,
    val isLoadingRecipeDetail: Boolean = false,

    @StringRes val errorMessageResId: Int? = null,
    @StringRes val userMessageResId: Int? = null
){
    val pageCount: Int
        get() = (
                (recipes.size + PAGE_SIZE - 1) /
                        PAGE_SIZE
                ).coerceAtLeast(1)

    val visibleRecipes: List<Recipe>
        get() = recipes
            .drop(currentPage * PAGE_SIZE)
            .take(PAGE_SIZE)

    companion object {
        const val PAGE_SIZE = 10
    }
}