package com.yilmaz.bimutfak.domain.error

sealed class HouseholdException : Exception() {

    class NameEmpty : HouseholdException()

    class InviteCodeEmpty : HouseholdException()

    class UserProfileNotFound : HouseholdException()

    class UserAlreadyInHousehold : HouseholdException()

    class InviteCodeNotFound : HouseholdException()

    class InviteCodeExpired : HouseholdException()

    class HouseholdNotFound : HouseholdException()

    class HouseholdFull : HouseholdException()

    class UserNotInHousehold : HouseholdException()

    class MemberNotInHousehold : HouseholdException()

    class OwnerCannotLeave : HouseholdException()

    class OwnerCannotBeRemoved : HouseholdException()

    class OnlyOwnerCanRemoveMember :
        HouseholdException()

    class RemoveMemberNotAuthorized :
        HouseholdException()

    class HouseholdInformationMismatch :
        HouseholdException()

    class AuthenticationRequired :
        HouseholdException()

    class OwnerInformationNotFound :
        HouseholdException()
}