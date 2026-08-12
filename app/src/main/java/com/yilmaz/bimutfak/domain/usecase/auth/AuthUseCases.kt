package com.yilmaz.bimutfak.domain.usecase.auth

import com.yilmaz.bimutfak.domain.model.User
import com.yilmaz.bimutfak.domain.repository.AuthRepositoryContract
import javax.inject.Inject

class CheckUserLoggedInUseCase @Inject constructor(
    private val repository: AuthRepositoryContract
) {

    operator fun invoke(): Boolean {
        return repository.isUserLoggedIn
    }
}

class LoginUseCase @Inject constructor(
    private val repository: AuthRepositoryContract
) {

    suspend operator fun invoke(
        email: String,
        password: String
    ): String {
        return repository.login(
            email = email,
            password = password
        )
    }
}

class RegisterUseCase @Inject constructor(
    private val repository: AuthRepositoryContract
) {

    suspend operator fun invoke(
        firstName: String,
        lastName: String,
        email: String,
        password: String
    ): String {
        return repository.register(
            firstName = firstName,
            lastName = lastName,
            email = email,
            password = password
        )
    }
}

class GetCurrentUserUseCase @Inject constructor(
    private val repository: AuthRepositoryContract
) {

    suspend operator fun invoke(): User? {
        return repository.getCurrentUser()
    }
}

class UpdateCurrentUserNameUseCase @Inject constructor(
    private val repository: AuthRepositoryContract
) {

    suspend operator fun invoke(
        firstName: String,
        lastName: String
    ) {
        repository.updateCurrentUserName(
            firstName = firstName,
            lastName = lastName
        )
    }
}

class LogoutUseCase @Inject constructor(
    private val repository: AuthRepositoryContract
) {

    operator fun invoke() {
        repository.logout()
    }
}