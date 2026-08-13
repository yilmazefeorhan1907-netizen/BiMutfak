package com.yilmaz.bimutfak.data.firestore

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.yilmaz.bimutfak.domain.model.Household
import com.yilmaz.bimutfak.domain.model.HouseholdInvite
import com.yilmaz.bimutfak.domain.model.User
import kotlinx.coroutines.tasks.await
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton
import com.yilmaz.bimutfak.domain.error.HouseholdException
// Hane, davet ve hane üyeleriyle ilgili Firestore işlemlerini yönetir.
@Singleton
class FirestoreHouseholdDataSource @Inject constructor(
    private val firestore: FirebaseFirestore
) {

    private val householdsCollection
        get() = firestore.collection("households")

    private val invitesCollection
        get() = firestore.collection("householdInvites")

    private val usersCollection
        get() = firestore.collection("users")

    // Kullanıcının bağlı olduğu haneyi getirir.
    suspend fun getHousehold(
        householdId: String
    ): Household? {
        return householdsCollection
            .document(householdId)
            .get()
            .await()
            .toObject(Household::class.java)
    }

    // Hanede bulunan kullanıcıların profil bilgilerini getirir.
    suspend fun getMembers(
        memberIds: List<String>
    ): List<User> {
        if (memberIds.isEmpty()) {
            return emptyList()
        }

        return memberIds.mapNotNull { userId ->
            usersCollection
                .document(userId)
                .get()
                .await()
                .toObject(User::class.java)
        }
    }

    // Yeni hane, davet kodu ve kullanıcı-hane bağlantısını birlikte kaydeder.
    suspend fun createHousehold(
        userId: String,
        householdName: String
    ): Household {
        val householdDocument =
            householdsCollection.document()

        val inviteCode = householdDocument.id
            .take(INVITE_CODE_LENGTH)
            .uppercase(Locale.ROOT)

        val createdAt = System.currentTimeMillis()

        val household = Household(
            id = householdDocument.id,
            name = householdName.trim(),
            ownerId = userId,
            memberIds = listOf(userId),
            inviteCode = inviteCode,
            createdAt = createdAt
        )

        val invite = HouseholdInvite(
            code = inviteCode,
            householdId = household.id,
            createdByUserId = userId,
            createdAt = createdAt,
            expiresAt = createdAt + INVITE_DURATION_MILLIS
        )

        val batch = firestore.batch()

        batch.set(
            householdDocument,
            household
        )

        batch.set(
            invitesCollection.document(inviteCode),
            invite
        )

        batch.update(
            usersCollection.document(userId),
            "householdId",
            household.id
        )

        batch.commit().await()

        return household
    }

    // Kullanıcıyı geçerli davet kodunun ait olduğu haneye ekler.
    suspend fun joinHousehold(
        userId: String,
        inviteCode: String
    ): Household {
        val normalizedCode = inviteCode
            .trim()
            .uppercase(Locale.ROOT)

        val userDocument =
            usersCollection.document(userId)

        val inviteDocument =
            invitesCollection.document(normalizedCode)

        return firestore.runTransaction { transaction ->
            val userSnapshot =
                transaction.get(userDocument)

            if (!userSnapshot.exists()) {
                throw HouseholdException
                    .UserProfileNotFound()
            }

            val currentHouseholdId =
                userSnapshot.getString("householdId")

            if (!currentHouseholdId.isNullOrBlank()) {
                throw HouseholdException
                    .UserAlreadyInHousehold()
            }

            val invite = transaction
                .get(inviteDocument)
                .toObject(HouseholdInvite::class.java)
                ?: throw HouseholdException
                    .InviteCodeNotFound()

            if (
                invite.expiresAt <=
                System.currentTimeMillis()
            ) {
                throw HouseholdException
                    .InviteCodeExpired()
            }

            val householdDocument =
                householdsCollection.document(
                    invite.householdId
                )

            val household = transaction
                .get(householdDocument)
                .toObject(Household::class.java)
                ?: throw HouseholdException
                    .HouseholdNotFound()

            if (
                household.memberIds.size >=
                MAX_HOUSEHOLD_MEMBER_COUNT
            ) {
                throw HouseholdException.HouseholdFull()
            }

            transaction.update(
                householdDocument,
                "memberIds",
                FieldValue.arrayUnion(userId)
            )

            transaction.update(
                userDocument,
                "householdId",
                household.id
            )

            household.copy(
                memberIds = (
                        household.memberIds + userId
                        ).distinct()
            )
        }.await()
    }
    // Yöneticinin bir üyeyi çıkarmasını veya üyenin kendi isteğiyle ayrılmasını sağlar.
    suspend fun removeMember(
        requestingUserId: String,
        memberId: String,
        householdId: String
    ): Household {
        val householdDocument =
            householdsCollection.document(householdId)

        val memberDocument =
            usersCollection.document(memberId)

        return firestore.runTransaction { transaction ->
            val household = transaction
                .get(householdDocument)
                .toObject(Household::class.java)
                ?: throw HouseholdException
                    .HouseholdNotFound()

            if (
                requestingUserId !in
                household.memberIds
            ) {
                throw HouseholdException
                    .UserNotInHousehold()
            }

            if (memberId !in household.memberIds) {
                throw HouseholdException
                    .MemberNotInHousehold()
            }

            if (memberId == household.ownerId) {
                throw HouseholdException
                    .OwnerCannotBeRemoved()
            }

            if (
                requestingUserId != household.ownerId &&
                requestingUserId != memberId
            ) {
                throw HouseholdException
                    .RemoveMemberNotAuthorized()
            }

            val memberSnapshot =
                transaction.get(memberDocument)

            if (
                memberSnapshot.getString("householdId") !=
                household.id
            ) {
                throw HouseholdException
                    .HouseholdInformationMismatch()
            }

            transaction.update(
                householdDocument,
                "memberIds",
                FieldValue.arrayRemove(memberId)
            )

            transaction.update(
                memberDocument,
                "householdId",
                null
            )

            household.copy(
                memberIds = household.memberIds
                    .filterNot { it == memberId }
            )
        }.await()
    }
    private companion object {

        const val MAX_HOUSEHOLD_MEMBER_COUNT = 3

        const val INVITE_CODE_LENGTH = 8

        const val INVITE_DURATION_MILLIS =
            7L * 24L * 60L * 60L * 1000L
    }
}