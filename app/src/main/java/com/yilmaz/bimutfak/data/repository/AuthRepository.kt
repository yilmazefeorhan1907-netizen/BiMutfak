package com.yilmaz.bimutfak.data.repository

import com.yilmaz.bimutfak.data.auth.FirebaseAuthDataSource
import javax.inject.Inject
import javax.inject.Singleton
import com.yilmaz.bimutfak.data.firestore.FirestoreUserDataSource
import com.yilmaz.bimutfak.domain.model.User

// Uygulama boyunca aynı DataSource örneğinin kullanılmasını sağlar.
@Singleton
class AuthRepository @Inject constructor(
    private val authDataSource: FirebaseAuthDataSource,
    private val userDataSource: FirestoreUserDataSource
) {

    val isUserLoggedIn: Boolean
        get() = authDataSource.currentUser != null

    val currentUserId: String?
        get() = authDataSource.currentUser?.uid

    suspend fun register(
        firstName: String,
        lastName: String,
        email: String,
        password: String
    ): String {
        val firebaseUser = authDataSource.register(
            email = email,
            password = password
        )

        val user = User(
            uid = firebaseUser.uid,
            firstName = firstName.trim(),
            lastName = lastName.trim(),
            email = email.trim(),
            householdId = null,
            createdAt = System.currentTimeMillis()
        )

        try {
            userDataSource.saveUser(user)
        } catch (exception: Exception) {

            // Firestore profil kaydı başarısızsa oluşturulan hesabı silmeyi dener.
            try {
                authDataSource.deleteUser(firebaseUser)
            } catch (_: Exception) {

                // Hesap silinemese bile cihazdaki oturumu açık bırakmaz.
                authDataSource.logout()
            }

            throw exception
        }

        return firebaseUser.uid
    }

    suspend fun login(
        email: String,
        password: String
    ): String {
        return authDataSource
            .login(email, password)
            .uid
    }
    // Oturumu açık olan kullanıcının Firestore profilini getirir.
    suspend fun getCurrentUser(): User? {
        val uid = currentUserId ?: return null

        return userDataSource.getUser(uid)
    }
    fun logout() {
        authDataSource.logout()
    }
    suspend fun updateCurrentUserName(
        firstName: String,
        lastName: String
    ) {
        val uid = currentUserId
            ?: error(
                "Profil güncelleme işlemi için kullanıcı oturumu gerekli."
            )

        userDataSource.updateUserName(
            uid = uid,
            firstName = firstName.trim(),
            lastName = lastName.trim()
        )
    }
}
