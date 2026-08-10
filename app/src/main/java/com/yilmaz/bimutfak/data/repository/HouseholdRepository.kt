package com.yilmaz.bimutfak.data.repository

import com.yilmaz.bimutfak.data.auth.FirebaseAuthDataSource
import com.yilmaz.bimutfak.data.firestore.FirestoreHouseholdDataSource
import com.yilmaz.bimutfak.data.firestore.FirestoreUserDataSource
import com.yilmaz.bimutfak.domain.model.Household
import com.yilmaz.bimutfak.domain.model.User
import javax.inject.Inject
import javax.inject.Singleton

// Hane işlemlerindeki kullanıcı kontrolünü ve uygulama kurallarını yönetir.
@Singleton
class HouseholdRepository @Inject constructor(
    private val authDataSource: FirebaseAuthDataSource,
    private val userDataSource: FirestoreUserDataSource,
    private val householdDataSource: FirestoreHouseholdDataSource
) {
    // Oturumu açık kullanıcının Firebase kimliğini ekran katmanına sunar.
    val currentUserId: String?
        get() = authDataSource.currentUser?.uid

    // Oturumu açık kullanıcının bağlı olduğu haneyi getirir.
    suspend fun getCurrentHousehold(): Household? {
        val currentUser = requireCurrentUser()

        val householdId = currentUser.householdId
            ?.takeIf { it.isNotBlank() }
            ?: return null

        return householdDataSource.getHousehold(
            householdId = householdId
        )
    }

    // Verilen hanede bulunan kullanıcıların profil bilgilerini getirir.
    suspend fun getMembers(
        household: Household
    ): List<User> {
        return householdDataSource.getMembers(
            memberIds = household.memberIds
        )
    }

    // Oturumu açık kullanıcı için yeni hane oluşturur.
    suspend fun createHousehold(
        householdName: String
    ): Household {
        val normalizedName = householdName.trim()

        require(normalizedName.isNotBlank()) {
            "Hane adı boş bırakılamaz."
        }

        val currentUser = requireCurrentUser()

        check(currentUser.householdId.isNullOrBlank()) {
            "Kullanıcı zaten bir haneye bağlı."
        }

        return householdDataSource.createHousehold(
            userId = currentUser.uid,
            householdName = normalizedName
        )
    }

    // Kullanıcıyı davet koduyla mevcut bir haneye ekler.
    suspend fun joinHousehold(
        inviteCode: String
    ): Household {
        val normalizedCode = inviteCode.trim()

        require(normalizedCode.isNotBlank()) {
            "Davet kodu boş bırakılamaz."
        }

        val currentUser = requireCurrentUser()

        check(currentUser.householdId.isNullOrBlank()) {
            "Kullanıcı zaten bir haneye bağlı."
        }

        return householdDataSource.joinHousehold(
            userId = currentUser.uid,
            inviteCode = normalizedCode
        )
    }

    // Kullanıcının kişisel veya ortak dolap ve sepet verilerinin sahibini belirler.
    suspend fun getDataOwnerId(): String {
        val currentUser = requireCurrentUser()

        val householdId = currentUser.householdId
            ?.takeIf { it.isNotBlank() }
            ?: return currentUser.uid

        val household = householdDataSource.getHousehold(
            householdId = householdId
        ) ?: error(
            "Kullanıcının bağlı olduğu hane bulunamadı."
        )

        return household.ownerId
            .takeIf { it.isNotBlank() }
            ?: error(
                "Hane yöneticisi bilgisi bulunamadı."
            )
    }

    // Normal kullanıcının kendi isteğiyle bağlı olduğu haneden ayrılmasını sağlar.
    suspend fun leaveHousehold(): Household {
        val currentUser = requireCurrentUser()

        val householdId = currentUser.householdId
            ?.takeIf { it.isNotBlank() }
            ?: error(
                "Kullanıcı bir haneye bağlı değil."
            )

        val household = householdDataSource.getHousehold(
            householdId = householdId
        ) ?: error("Hane bulunamadı.")

        check(currentUser.uid != household.ownerId) {
            "Hane yöneticisi haneden ayrılamaz."
        }

        return householdDataSource.removeMember(
            requestingUserId = currentUser.uid,
            memberId = currentUser.uid,
            householdId = household.id
        )
    }

    // Hane yöneticisinin seçilen üyeyi haneden çıkarmasını sağlar.
    suspend fun removeMember(
        memberId: String
    ): Household {
        val currentUser = requireCurrentUser()

        val householdId = currentUser.householdId
            ?.takeIf { it.isNotBlank() }
            ?: error(
                "Kullanıcı bir haneye bağlı değil."
            )

        val household = householdDataSource.getHousehold(
            householdId = householdId
        ) ?: error("Hane bulunamadı.")

        check(currentUser.uid == household.ownerId) {
            "Yalnızca hane yöneticisi üye çıkarabilir."
        }

        check(memberId != household.ownerId) {
            "Hane yöneticisi haneden çıkarılamaz."
        }

        return householdDataSource.removeMember(
            requestingUserId = currentUser.uid,
            memberId = memberId,
            householdId = household.id
        )
    }

    // Geçersiz veya eksik oturum durumlarına karşı güvenlik kontrolü sağlar.
    private suspend fun requireCurrentUser(): User {
        val userId = authDataSource.currentUser?.uid
            ?: error(
                "Hane işlemi için kullanıcı oturumu gerekli."
            )

        return userDataSource.getUser(userId)
            ?: error(
                "Kullanıcı profili bulunamadı."
            )
    }
}