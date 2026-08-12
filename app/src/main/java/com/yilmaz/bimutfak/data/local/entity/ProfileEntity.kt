package com.yilmaz.bimutfak.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "profiles")
data class ProfileEntity(
    @PrimaryKey
    val uid: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val householdId: String?,
    val createdAt: Long,
    val cachedAt: Long
)