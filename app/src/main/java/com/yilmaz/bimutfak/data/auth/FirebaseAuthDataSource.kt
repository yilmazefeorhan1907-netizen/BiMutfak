package com.yilmaz.bimutfak.data.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton
import com.yilmaz.bimutfak.domain.error.AuthException

// Uygulama boyunca aynı DataSource örneğinin kullanılmasını sağlar.
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
            ?: throw AuthException.UserInformationMissing()
    }

    suspend fun login(
        email: String,
        password: String
    ): FirebaseUser {
        val authResult = firebaseAuth
            .signInWithEmailAndPassword(email, password)
            .await()

        return authResult.user
            ?: throw AuthException.UserInformationMissing()
    }

    // Profil kaydı başarısız olursa yeni oluşturulan hesabı geri alır.
    suspend fun deleteUser(user: FirebaseUser) {
        user.delete().await()
    }
    fun logout() {
        firebaseAuth.signOut()
    }
}