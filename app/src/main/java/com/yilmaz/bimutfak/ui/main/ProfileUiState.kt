package com.yilmaz.bimutfak.ui.main

import androidx.annotation.StringRes

// Profil ekranında gösterilecek kişisel arayüz verilerini tutar.
data class ProfileUiState(
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",

    // Düzenleme penceresindeki geçici değerler
    val editableFirstName: String = "",
    val editableLastName: String = "",

    val dailyMenu: List<String> = emptyList(),
    val favoriteRecipes: List<String> = emptyList(),

    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val isEditProfileDialogVisible: Boolean = false,

    @StringRes val errorMessageResId: Int? = null
)