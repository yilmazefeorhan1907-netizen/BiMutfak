package com.yilmaz.bimutfak.domain.model

// Kullanıcının dolabında bulunan bir ürünü temsil eder.
data class PantryItem(
    val id: String = "",
    val name: String = "",
    val quantity: Double = 0.0,
    val unit: String = "",
    val section: PantrySection = PantrySection.DRY_FOOD,
    val imageUrl: String = "",
    val expirationDate: Long? = null,
    val createdAt: Long = 0L
)