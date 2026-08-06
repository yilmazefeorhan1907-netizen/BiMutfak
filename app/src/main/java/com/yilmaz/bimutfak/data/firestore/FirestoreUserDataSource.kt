package com.yilmaz.bimutfak.data.firestore

import com.google.firebase.firestore.FirebaseFirestore
import com.yilmaz.bimutfak.domain.model.User
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

// Kullanıcı profillerinin Firestore'a yazılmasını yönetir.
@Singleton
class FirestoreUserDataSource @Inject constructor(
    private val firestore: FirebaseFirestore
) {

    suspend fun saveUser(user: User) {
        firestore
            .collection("users")
            .document(user.uid)
            .set(user)
            .await()
    }
}