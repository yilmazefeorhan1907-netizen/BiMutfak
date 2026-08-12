package com.yilmaz.bimutfak.domain.usecase.household

import com.yilmaz.bimutfak.domain.model.Household
import com.yilmaz.bimutfak.domain.model.User
import com.yilmaz.bimutfak.domain.repository.HouseholdRepositoryContract
import javax.inject.Inject

class GetCurrentUserIdUseCase @Inject constructor(
    private val repository: HouseholdRepositoryContract
) {

    operator fun invoke(): String? {
        return repository.currentUserId
    }
}

class GetCurrentHouseholdUseCase @Inject constructor(
    private val repository: HouseholdRepositoryContract
) {

    suspend operator fun invoke(): Household? {
        return repository.getCurrentHousehold()
    }
}

class GetHouseholdMembersUseCase @Inject constructor(
    private val repository: HouseholdRepositoryContract
) {

    suspend operator fun invoke(
        household: Household
    ): List<User> {
        return repository.getMembers(household)
    }
}

class CreateHouseholdUseCase @Inject constructor(
    private val repository: HouseholdRepositoryContract
) {

    suspend operator fun invoke(
        householdName: String
    ): Household {
        return repository.createHousehold(householdName)
    }
}

class JoinHouseholdUseCase @Inject constructor(
    private val repository: HouseholdRepositoryContract
) {

    suspend operator fun invoke(
        inviteCode: String
    ): Household {
        return repository.joinHousehold(inviteCode)
    }
}

class LeaveHouseholdUseCase @Inject constructor(
    private val repository: HouseholdRepositoryContract
) {

    suspend operator fun invoke(): Household {
        return repository.leaveHousehold()
    }
}

class RemoveHouseholdMemberUseCase @Inject constructor(
    private val repository: HouseholdRepositoryContract
) {

    suspend operator fun invoke(
        memberId: String
    ): Household {
        return repository.removeMember(memberId)
    }
}