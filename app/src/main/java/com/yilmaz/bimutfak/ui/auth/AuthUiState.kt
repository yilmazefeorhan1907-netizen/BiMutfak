package com.yilmaz.bimutfak.ui.auth

import androidx.annotation.StringRes

// Bu sınıfın temel amacı işlem yapmak değil, veri taşımaktır, veri modeline daha yakındır.
// Giriş ve kayıt ekranlarında gösterilen güncel arayüz durumunu tutar.
data class AuthUiState(
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isPasswordVisible: Boolean = false,
    val isConfirmPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,

// Bu değerin yalnızca R.string türünde bir metin kaynağı olmasını belirtir.
    @StringRes val errorMessageResId: Int? = null,
    val isAuthenticated: Boolean = false
)