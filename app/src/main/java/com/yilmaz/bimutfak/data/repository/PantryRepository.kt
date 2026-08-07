package com.yilmaz.bimutfak.data.repository

import com.yilmaz.bimutfak.data.auth.FirebaseAuthDataSource
import com.yilmaz.bimutfak.data.firestore.FirestorePantryDataSource
import com.yilmaz.bimutfak.domain.model.PantryItem
import com.yilmaz.bimutfak.domain.model.PantrySection
import javax.inject.Inject
import javax.inject.Singleton

// Dolap verileriyle ilgili uygulama işlemlerini yönetir.
@Singleton
class PantryRepository @Inject constructor(
    private val authDataSource: FirebaseAuthDataSource,
    private val pantryDataSource: FirestorePantryDataSource
) {

    suspend fun getItems(): List<PantryItem> {
        return pantryDataSource.getItems(
            userId = requireUserId()
        )
    }

    suspend fun addItem(
        name: String,
        quantity: Double,
        unit: String,
        section: PantrySection
    ): PantryItem {
        val item = PantryItem(
            name = name.trim(),
            quantity = quantity,
            unit = unit.trim(),
            section = section,
            createdAt = System.currentTimeMillis()
        )

        return pantryDataSource.saveItem(
            userId = requireUserId(),
            item = item
        )
    }

    suspend fun deleteItem(
        itemId: String
    ) {
        pantryDataSource.deleteItem(
            userId = requireUserId(),
            itemId = itemId
        )
    }

    private fun requireUserId(): String {
        return authDataSource.currentUser?.uid
            ?: error("Dolap işlemi için kullanıcı oturumu gerekli.")
        // İlerleyen aşamalarda oluşabilecek geçersiz oturum durumlarına karşı güvenlik kontrolü sağlar.
    }
}