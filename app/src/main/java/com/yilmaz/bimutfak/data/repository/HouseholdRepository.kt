package com.yilmaz.bimutfak.data.repository

import com.yilmaz.bimutfak.data.auth.FirebaseAuthDataSource
import com.yilmaz.bimutfak.data.firestore.FirestoreHouseholdDataSource
import com.yilmaz.bimutfak.data.firestore.FirestoreUserDataSource
import com.yilmaz.bimutfak.domain.error.HouseholdException
import com.yilmaz.bimutfak.domain.model.Household
import com.yilmaz.bimutfak.domain.model.User
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HouseholdRepository @Inject constructor(
    private val authDataSource: FirebaseAuthDataSource,
    private val userDataSource: FirestoreUserDataSource,
    private val householdDataSource:
    FirestoreHouseholdDataSource
) {

    val currentUserId: String?
        get() = authDataSource.currentUser?.uid

    suspend fun getCurrentHousehold(): Household? {
        val currentUser = requireCurrentUser()

        val householdId = currentUser.householdId
            ?.takeIf { it.isNotBlank() }
            ?: return null

        return householdDataSource.getHousehold(
            householdId = householdId
        )
    }

    suspend fun getMembers(
        household: Household
    ): List<User> {
        return householdDataSource.getMembers(
            memberIds = household.memberIds
        )
    }

    suspend fun createHousehold(
        householdName: String
    ): Household {
        val normalizedName = householdName.trim()

        if (normalizedName.isBlank()) {
            throw HouseholdException.NameEmpty()
        }

        val currentUser = requireCurrentUser()

        if (
            !currentUser.householdId
                .isNullOrBlank()
        ) {
            throw HouseholdException
                .UserAlreadyInHousehold()
        }

        return householdDataSource.createHousehold(
            userId = currentUser.uid,
            householdName = normalizedName
        )
    }

    suspend fun joinHousehold(
        inviteCode: String
    ): Household {
        val normalizedCode = inviteCode.trim()

        if (normalizedCode.isBlank()) {
            throw HouseholdException
                .InviteCodeEmpty()
        }

        val currentUser = requireCurrentUser()

        if (
            !currentUser.householdId
                .isNullOrBlank()
        ) {
            throw HouseholdException
                .UserAlreadyInHousehold()
        }

        return householdDataSource.joinHousehold(
            userId = currentUser.uid,
            inviteCode = normalizedCode
        )
    }

    suspend fun getDataOwnerId(): String {
        val currentUser = requireCurrentUser()

        val householdId = currentUser.householdId
            ?.takeIf { it.isNotBlank() }
            ?: return currentUser.uid

        val household =
            householdDataSource.getHousehold(
                householdId = householdId
            ) ?: throw HouseholdException
                .HouseholdNotFound()

        return household.ownerId
            .takeIf { it.isNotBlank() }
            ?: throw HouseholdException
                .OwnerInformationNotFound()
    }

    suspend fun leaveHousehold(): Household {
        val currentUser = requireCurrentUser()

        val householdId = currentUser.householdId
            ?.takeIf { it.isNotBlank() }
            ?: throw HouseholdException
                .UserNotInHousehold()

        val household =
            householdDataSource.getHousehold(
                householdId = householdId
            ) ?: throw HouseholdException
                .HouseholdNotFound()

        if (currentUser.uid == household.ownerId) {
            throw HouseholdException
                .OwnerCannotLeave()
        }

        return householdDataSource.removeMember(
            requestingUserId = currentUser.uid,
            memberId = currentUser.uid,
            householdId = household.id
        )
    }

    suspend fun removeMember(
        memberId: String
    ): Household {
        val currentUser = requireCurrentUser()

        val householdId = currentUser.householdId
            ?.takeIf { it.isNotBlank() }
            ?: throw HouseholdException
                .UserNotInHousehold()

        val household =
            householdDataSource.getHousehold(
                householdId = householdId
            ) ?: throw HouseholdException
                .HouseholdNotFound()

        if (currentUser.uid != household.ownerId) {
            throw HouseholdException
                .OnlyOwnerCanRemoveMember()
        }

        if (memberId == household.ownerId) {
            throw HouseholdException
                .OwnerCannotBeRemoved()
        }

        return householdDataSource.removeMember(
            requestingUserId = currentUser.uid,
            memberId = memberId,
            householdId = household.id
        )
    }

    private suspend fun requireCurrentUser(): User {
        val userId =
            authDataSource.currentUser?.uid
                ?: throw HouseholdException
                    .AuthenticationRequired()

        return userDataSource.getUser(userId)
            ?: throw HouseholdException
                .UserProfileNotFound()
    }
}