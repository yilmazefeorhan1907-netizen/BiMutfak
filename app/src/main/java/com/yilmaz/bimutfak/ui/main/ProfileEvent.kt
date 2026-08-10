package com.yilmaz.bimutfak.ui.main

// Kullanıcının profil ekranında gerçekleştirebileceği işlemleri tanımlar.
sealed interface ProfileEvent {

    data class FirstNameChanged(
        val firstName: String
    ) : ProfileEvent

    data class LastNameChanged(
        val lastName: String
    ) : ProfileEvent

    data object EditProfileRequested : ProfileEvent

    data object EditProfileDismissed : ProfileEvent

    data object SaveProfileClicked : ProfileEvent

    data object ClearError : ProfileEvent
}