package com.yilmaz.bimutfak.domain.repository

import com.yilmaz.bimutfak.domain.model.BasketItem

interface BasketRepositoryContract {

    suspend fun getItems(): List<BasketItem>

    suspend fun addItem(
        name: String,
        quantity: Double,
        unit: String
    ): BasketItem

    suspend fun setItemChecked(
        item: BasketItem,
        checked: Boolean
    ): BasketItem

    suspend fun deleteItem(
        itemId: String
    )
}