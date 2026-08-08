package com.yilmaz.bimutfak.domain.model

// Alışveriş listesine eklenen bir ürünü temsil eder.
data class BasketItem(
    val id: String = "",
    val name: String = "",
    val quantity: Double = 0.0,
    val unit: String = "",
    val checked: Boolean = false,
    val createdAt: Long = 0L
)