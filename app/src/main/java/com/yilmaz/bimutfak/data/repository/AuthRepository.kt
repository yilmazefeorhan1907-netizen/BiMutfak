package com.yilmaz.bimutfak.data.repository

import com.yilmaz.bimutfak.data.auth.FirebaseAuthDataSource
import com.yilmaz.bimutfak.data.firestore.FirestoreUserDataSource
import com.yilmaz.bimutfak.data.local.dao.ProfileDao
import com.yilmaz.bimutfak.data.local.mapper.toProfileEntity
import com.yilmaz.bimutfak.data.local.mapper.toUser
import com.yilmaz.bimutfak.domain.model.User
import javax.inject.Inject
import javax.inject.Singleton
import com.yilmaz.bimutfak.domain.error.AuthException

@Singleton
class AuthRepository @Inject constructor(
    private val authDataSource: FirebaseAuthDataSource,
    private val userDataSource: FirestoreUserDataSource,
    private val profileDao: ProfileDao
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
            try {
                authDataSource.deleteUser(firebaseUser)
            } catch (_: Exception) {
                authDataSource.logout()
            }

            throw exception
        }

        try {
            profileDao.saveProfile(
                user.toProfileEntity(
                    cachedAt = System.currentTimeMillis()
                )
            )
        } catch (_: Exception) {
            // Local cache failure does not cancel registration.
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

    suspend fun getCurrentUser(): User? {
        val uid = currentUserId ?: return null
        val currentTime = System.currentTimeMillis()
        val cachedProfile = profileDao.getProfile(uid)

        val isCacheFresh =
            cachedProfile != null &&
                    currentTime - cachedProfile.cachedAt <
                    PROFILE_CACHE_DURATION_MILLIS

        if (isCacheFresh) {
            return cachedProfile?.toUser()
        }

        return try {
            val remoteUser = userDataSource.getUser(uid)

            if (remoteUser != null) {
                profileDao.saveProfile(
                    remoteUser.toProfileEntity(
                        cachedAt = currentTime
                    )
                )
            }

            remoteUser ?: cachedProfile?.toUser()
        } catch (exception: Exception) {
            cachedProfile?.toUser()
                ?: throw exception
        }
    }

    fun logout() {
        authDataSource.logout()
    }

    suspend fun updateCurrentUserName(
        firstName: String,
        lastName: String
    ) {
                val uid = currentUserId
            ?: throw AuthException.AuthenticationRequired()


        val cleanFirstName = firstName.trim()
        val cleanLastName = lastName.trim()

        userDataSource.updateUserName(
            uid = uid,
            firstName = cleanFirstName,
            lastName = cleanLastName
        )

        try {
            val cachedProfile = profileDao.getProfile(uid)

            if (cachedProfile != null) {
                profileDao.saveProfile(
                    cachedProfile.copy(
                        firstName = cleanFirstName,
                        lastName = cleanLastName,
                        cachedAt =
                            System.currentTimeMillis()
                    )
                )
            }
        } catch (_: Exception) {
            // Firestore update remains successful.
        }
    }

    companion object {
        private const val PROFILE_CACHE_DURATION_MILLIS =
            60 * 60 * 1000L
    }
}