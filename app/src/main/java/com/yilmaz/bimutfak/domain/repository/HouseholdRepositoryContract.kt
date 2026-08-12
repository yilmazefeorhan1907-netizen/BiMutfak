package com.yilmaz.bimutfak.domain.repository

import com.yilmaz.bimutfak.domain.model.Household
import com.yilmaz.bimutfak.domain.model.User

interface HouseholdRepositoryContract {

    val currentUserId: String?

    suspend fun getCurrentHousehold(): Household?

    suspend fun getMembers(
        household: Household
    ): List<User>

    suspend fun createHousehold(
        householdName: String
    ): Household

    suspend fun joinHousehold(
        inviteCode: String
    ): Household

    suspend fun getDataOwnerId(): String

    suspend fun leaveHousehold(): Household

    suspend fun removeMember(
        memberId: String
    ): Household
}