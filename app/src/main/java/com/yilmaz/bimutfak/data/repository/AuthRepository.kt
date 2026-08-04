package com.yilmaz.bimutfak.data.repository

import com.yilmaz.bimutfak.data.auth.FirebaseAuthDataSource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val authDataSource: FirebaseAuthDataSource
) {

    val isUserLoggedIn: Boolean
        get() = authDataSource.currentUser != null

    val currentUserId: String?
        get() = authDataSource.currentUser?.uid

    suspend fun register(
        email: String,
        password: String
    ): String {
        return authDataSource
            .register(email, password)
            .uid
    }

    suspend fun login(
        email: String,
        password: String
    ): String {
        return authDataSource
            .login(email, password)
            .uid
    }

    fun logout() {
        authDataSource.logout()
    }
}
