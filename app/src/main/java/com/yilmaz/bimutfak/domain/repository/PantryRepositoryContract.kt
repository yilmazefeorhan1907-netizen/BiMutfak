package com.yilmaz.bimutfak.domain.repository

import com.yilmaz.bimutfak.domain.model.PantryItem
import com.yilmaz.bimutfak.domain.model.PantrySection

interface PantryRepositoryContract {

    suspend fun getItems(): List<PantryItem>

    suspend fun addItem(
        name: String,
        quantity: Double,
        unit: String,
        section: PantrySection
    ): PantryItem

    suspend fun deleteItem(
        itemId: String
    )
}