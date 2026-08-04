package com.yilmaz.bimutfak.data.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseAuthDataSource @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) {

    val currentUser: FirebaseUser?
        get() = firebaseAuth.currentUser

    suspend fun register(
        email: String,
        password: String
    ): FirebaseUser {
        val authResult = firebaseAuth
            .createUserWithEmailAndPassword(email, password)
            .await()

        return authResult.user
            ?: error("Firebase kullanıcı bilgisi döndürmedi.")
    }

    suspend fun login(
        email: String,
        password: String
    ): FirebaseUser {
        val authResult = firebaseAuth
            .signInWithEmailAndPassword(email, password)
            .await()

        return authResult.user
            ?: error("Firebase kullanıcı bilgisi döndürmedi.")
    }

    fun logout() {
        firebaseAuth.signOut()
    }
}