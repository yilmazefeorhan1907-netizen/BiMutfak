package com.yilmaz.bimutfak.ui.main

import androidx.annotation.StringRes
import com.yilmaz.bimutfak.domain.model.Recipe

// Profil ekranında gösterilecek kişisel arayüz verilerini tutar.
data class ProfileUiState(
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",

    val editableFirstName: String = "",
    val editableLastName: String = "",

    val dailyMenu: List<Recipe> = emptyList(),
    val favoriteRecipes: List<Recipe> = emptyList(),
    val selectedRecipe: Recipe? = null,

    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val isEditProfileDialogVisible: Boolean = false,

    @StringRes val errorMessageResId: Int? = null
)