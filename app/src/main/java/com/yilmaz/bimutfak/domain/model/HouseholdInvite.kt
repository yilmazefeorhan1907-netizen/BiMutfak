package com.yilmaz.bimutfak.domain.model

// Bir kullanıcının davet koduyla haneye katılabilmesini sağlayan veriyi temsil eder.
data class HouseholdInvite(
    val code: String = "",
    val householdId: String = "",
    val createdByUserId: String = "",
    val createdAt: Long = 0L,
    val expiresAt: Long = 0L
)