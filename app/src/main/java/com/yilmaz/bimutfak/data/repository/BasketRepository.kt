package com.yilmaz.bimutfak.data.repository

import com.yilmaz.bimutfak.data.auth.FirebaseAuthDataSource
import com.yilmaz.bimutfak.data.firestore.FirestoreBasketDataSource
import com.yilmaz.bimutfak.domain.model.BasketItem
import javax.inject.Inject
import javax.inject.Singleton

// Alışveriş listesiyle ilgili uygulama işlemlerini yönetir.
@Singleton
class BasketRepository @Inject constructor(
    private val authDataSource: FirebaseAuthDataSource,
    private val basketDataSource: FirestoreBasketDataSource
) {

    suspend fun getItems(): List<BasketItem> {
        return basketDataSource.getItems(
            userId = requireUserId()
        )
    }

    suspend fun addItem(
        name: String,
        quantity: Double,
        unit: String
    ): BasketItem {
        val item = BasketItem(
            name = name.trim(),
            quantity = quantity,
            unit = unit.trim(),
            checked = false,
            createdAt = System.currentTimeMillis()
        )

        return basketDataSource.saveItem(
            userId = requireUserId(),
            item = item
        )
    }

    suspend fun setItemChecked(
        item: BasketItem,
        checked: Boolean
    ): BasketItem {
        return basketDataSource.saveItem(
            userId = requireUserId(),
            item = item.copy(
                checked = checked
            )
        )
    }

    suspend fun deleteItem(
        itemId: String
    ) {
        basketDataSource.deleteItem(
            userId = requireUserId(),
            itemId = itemId
        )
    }

    // İleride oluşabilecek geçersiz oturum durumlarına karşı güvenlik kontrolü.
    private fun requireUserId(): String {
        return authDataSource.currentUser?.uid
            ?: error(
                "Alışveriş listesi işlemi için kullanıcı oturumu gerekli."
            )
    }
}