package com.yilmaz.bimutfak.domain.model

// Uygulamada oluşturulan bir haneyi ve üyelerini temsil eder.
data class Household(
    val id: String = "",
    val name: String = "",
    val ownerId: String = "",
    val memberIds: List<String> = emptyList(),
    val inviteCode: String = "",
    val createdAt: Long = 0L
)