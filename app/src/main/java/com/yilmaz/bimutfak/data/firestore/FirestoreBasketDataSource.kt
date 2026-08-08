package com.yilmaz.bimutfak.data.firestore

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.yilmaz.bimutfak.domain.model.BasketItem
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

// Alışveriş listesi ürünlerinin Firestore işlemlerini yönetir.
@Singleton
class FirestoreBasketDataSource @Inject constructor(
    private val firestore: FirebaseFirestore
) {

    private fun basketCollection(
        userId: String
    ): CollectionReference {
        return firestore
            .collection("users")
            .document(userId)
            .collection("basketItems")
    }

    suspend fun getItems(
        userId: String
    ): List<BasketItem> {
        return basketCollection(userId)
            .orderBy(
                "createdAt",
                Query.Direction.DESCENDING
            )
            .get()
            .await()
            .toObjects(BasketItem::class.java)
    }

    suspend fun saveItem(
        userId: String,
        item: BasketItem
    ): BasketItem {
        val document = if (item.id.isBlank()) {
            basketCollection(userId).document()
        } else {
            basketCollection(userId).document(item.id)
        }

        val savedItem = item.copy(
            id = document.id
        )

        document
            .set(savedItem)
            .await()

        return savedItem
    }

    suspend fun deleteItem(
        userId: String,
        itemId: String
    ) {
        basketCollection(userId)
            .document(itemId)
            .delete()
            .await()
    }
}