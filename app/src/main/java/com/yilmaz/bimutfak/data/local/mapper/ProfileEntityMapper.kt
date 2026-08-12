package com.yilmaz.bimutfak.data.local.mapper

import com.yilmaz.bimutfak.data.local.entity.ProfileEntity
import com.yilmaz.bimutfak.domain.model.User

fun User.toProfileEntity(
    cachedAt: Long
): ProfileEntity {
    return ProfileEntity(
        uid = uid,
        firstName = firstName,
        lastName = lastName,
        email = email,
        householdId = householdId,
        createdAt = createdAt,
        cachedAt = cachedAt
    )
}

fun ProfileEntity.toUser(): User {
    return User(
        uid = uid,
        firstName = firstName,
        lastName = lastName,
        email = email,
        householdId = householdId,
        createdAt = createdAt
    )
}