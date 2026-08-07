package com.yilmaz.bimutfak.ui.main

// Profil ekranında gösterilecek kişisel arayüz verilerini tutar.
data class ProfileUiState(
    val firstName: String = "",
    val dailyMenu: List<String> = emptyList(),
    val favoriteRecipes: List<String> = emptyList(),
    val isLoading: Boolean = false
)