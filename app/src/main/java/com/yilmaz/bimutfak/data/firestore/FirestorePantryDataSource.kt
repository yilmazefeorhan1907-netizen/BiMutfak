package com.yilmaz.bimutfak.data.firestore

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.yilmaz.bimutfak.domain.model.PantryItem
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

// Dolap ürünlerinin Firestore'a yazılmasını ve okunmasını yönetir.
@Singleton
class FirestorePantryDataSource @Inject constructor(
    private val firestore: FirebaseFirestore
) {

    private fun pantryCollection(
        userId: String
    ): CollectionReference {
        return firestore
            .collection("users")
            .document(userId)
            .collection("pantryItems")
    }

    suspend fun getItems(
        userId: String
    ): List<PantryItem> {
        return pantryCollection(userId)
            .orderBy(
                "createdAt",
                Query.Direction.DESCENDING
            )
            .get()
            .await()
            .toObjects(PantryItem::class.java)
    }

    suspend fun saveItem(
        userId: String,
        item: PantryItem
    ): PantryItem {
        val document = if (item.id.isBlank()) {
            pantryCollection(userId).document()
        } else {
            pantryCollection(userId).document(item.id)
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
        pantryCollection(userId)
            .document(itemId)
            .delete()
            .await()
    }
}