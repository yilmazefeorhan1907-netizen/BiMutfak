package com.yilmaz.bimutfak.data.firestore

import com.google.firebase.firestore.FirebaseFirestore
import com.yilmaz.bimutfak.domain.model.Recipe
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

// Kullanıcının favori tariflerini ve günlük menüsünü Firestore'da yönetir.
@Singleton
class FirestoreRecipeSelectionDataSource @Inject constructor(
    private val firestore: FirebaseFirestore
) {

    suspend fun getFavoriteRecipes(
        userId: String
    ): List<Recipe> {
        return getRecipes(
            userId = userId,
            collectionName = FAVORITE_RECIPES_COLLECTION
        )
    }

    suspend fun getDailyMenu(
        userId: String
    ): List<Recipe> {
        return getRecipes(
            userId = userId,
            collectionName = DAILY_MENU_COLLECTION
        )
    }

    suspend fun saveFavoriteRecipe(
        userId: String,
        recipe: Recipe
    ) {
        saveRecipe(
            userId = userId,
            collectionName = FAVORITE_RECIPES_COLLECTION,
            recipe = recipe
        )
    }

    suspend fun saveDailyMenuRecipe(
        userId: String,
        recipe: Recipe
    ) {
        saveRecipe(
            userId = userId,
            collectionName = DAILY_MENU_COLLECTION,
            recipe = recipe
        )
    }

    suspend fun deleteFavoriteRecipe(
        userId: String,
        recipeId: String
    ) {
        deleteRecipe(
            userId = userId,
            collectionName = FAVORITE_RECIPES_COLLECTION,
            recipeId = recipeId
        )
    }

    suspend fun deleteDailyMenuRecipe(
        userId: String,
        recipeId: String
    ) {
        deleteRecipe(
            userId = userId,
            collectionName = DAILY_MENU_COLLECTION,
            recipeId = recipeId
        )
    }

    private suspend fun getRecipes(
        userId: String,
        collectionName: String
    ): List<Recipe> {
        return firestore
            .collection(USERS_COLLECTION)
            .document(userId)
            .collection(collectionName)
            .get()
            .await()
            .documents
            .mapNotNull { document ->
                document.toObject(Recipe::class.java)
            }
    }

    private suspend fun saveRecipe(
        userId: String,
        collectionName: String,
        recipe: Recipe
    ) {
        firestore
            .collection(USERS_COLLECTION)
            .document(userId)
            .collection(collectionName)
            .document(recipe.id)
            .set(recipe)
            .await()
    }

    private suspend fun deleteRecipe(
        userId: String,
        collectionName: String,
        recipeId: String
    ) {
        firestore
            .collection(USERS_COLLECTION)
            .document(userId)
            .collection(collectionName)
            .document(recipeId)
            .delete()
            .await()
    }

    companion object {
        private const val USERS_COLLECTION = "users"
        private const val FAVORITE_RECIPES_COLLECTION =
            "favoriteRecipes"
        private const val DAILY_MENU_COLLECTION =
            "dailyMenu"
    }
}