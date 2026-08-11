package com.yilmaz.bimutfak.ui.main

// Kullanıcının profil ekranında gerçekleştirebileceği işlemleri tanımlar.
sealed interface ProfileEvent {

    data class FirstNameChanged(
        val firstName: String
    ) : ProfileEvent

    data class LastNameChanged(
        val lastName: String
    ) : ProfileEvent

    data class RecipeClicked(
        val recipeId: String
    ) : ProfileEvent

    data class RemoveDailyMenuRecipeClicked(
        val recipeId: String
    ) : ProfileEvent

    data class RemoveFavoriteRecipeClicked(
        val recipeId: String
    ) : ProfileEvent

    data object RecipeDetailDismissed : ProfileEvent

    data object EditProfileRequested : ProfileEvent

    data object EditProfileDismissed : ProfileEvent

    data object SaveProfileClicked : ProfileEvent

    data object ClearError : ProfileEvent
}