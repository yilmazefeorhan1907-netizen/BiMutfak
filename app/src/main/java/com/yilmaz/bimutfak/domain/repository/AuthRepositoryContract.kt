package com.yilmaz.bimutfak.domain.repository

import com.yilmaz.bimutfak.domain.model.User

interface AuthRepositoryContract {

    val isUserLoggedIn: Boolean

    val currentUserId: String?

    suspend fun register(
        firstName: String,
        lastName: String,
        email: String,
        password: String
    ): String

    suspend fun login(
        email: String,
        password: String
    ): String

    suspend fun getCurrentUser(): User?

    fun logout()

    suspend fun updateCurrentUserName(
        firstName: String,
        lastName: String
    )
}