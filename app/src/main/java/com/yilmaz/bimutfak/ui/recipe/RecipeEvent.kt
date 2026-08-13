package com.yilmaz.bimutfak.ui.recipe

// Kullanıcının Bi’Tarif ekranında gerçekleştirebileceği işlemleri tanımlar.
sealed interface RecipeEvent {

    data class CuisineSelected(
        val cuisine: String
    ) : RecipeEvent

    data class SearchQueryChanged(
        val query: String
    ) : RecipeEvent

    data class RecipeClicked(
        val recipeId: String
    ) : RecipeEvent

    data class FavoriteClicked(
        val recipeId: String
    ) : RecipeEvent

    data class DailyMenuClicked(
        val recipeId: String
    ) : RecipeEvent

    data object PreviousPageClicked : RecipeEvent

    data object NextPageClicked : RecipeEvent

    data object RecipeDetailDismissed : RecipeEvent

    data object RetryClicked : RecipeEvent

    data object ClearMessage : RecipeEvent
}