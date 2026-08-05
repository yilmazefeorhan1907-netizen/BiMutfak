package com.yilmaz.bimutfak.ui.auth

// Kullanıcının giriş ve kayıt ekranlarında gerçekleştirebileceği işlemleri tanımlar.
// AuthEvent doğrudan nesne üretmek için değil, bütün Authentication olaylarının ortak türünü belirtmek için kullanılıyor
sealed interface AuthEvent {

    // E-posta alanındaki yeni değeri ViewModel'e gönderir.
    data class EmailChanged(
        val email: String
    ) : AuthEvent

    // Şifre alanındaki yeni değeri ViewModel'e gönderir.
    data class PasswordChanged(
        val password: String
    ) : AuthEvent

    // Şifre tekrar alanındaki yeni değeri ViewModel'e gönderir.
    data class ConfirmPasswordChanged(
        val confirmPassword: String
    ) : AuthEvent

    // Şifrenin görünürlük durumunu değiştirir.
    data object TogglePasswordVisibility : AuthEvent

    // Tekrar girilen şifrenin görünürlük durumunu değiştirir.
    data object ToggleConfirmPasswordVisibility : AuthEvent

    // Kullanıcının giriş butonuna bastığını bildirir.
    data object LoginClicked : AuthEvent

    // Kullanıcının hesap oluşturma butonuna bastığını bildirir.
    data object RegisterClicked : AuthEvent

    // Gösterilmiş hata mesajını temizler.
    data object ClearError : AuthEvent
}