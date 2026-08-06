package com.yilmaz.bimutfak.domain.model

// Firestore'da saklanacak kullanıcı profil bilgilerini temsil eder.
data class User(
    val uid: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val householdId: String? = null,
    val createdAt: Long = 0L
)