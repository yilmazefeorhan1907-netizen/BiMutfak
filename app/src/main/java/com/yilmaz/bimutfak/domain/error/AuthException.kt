package com.yilmaz.bimutfak.domain.error

sealed class AuthException : Exception() {

    class UserInformationMissing :
        AuthException()

    class AuthenticationRequired :
        AuthException()
}