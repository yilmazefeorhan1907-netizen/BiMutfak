package com.yilmaz.bimutfak.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.yilmaz.bimutfak.data.local.converter.RecipeTypeConverters
import com.yilmaz.bimutfak.domain.model.RecipeIngredient

@Entity(tableName = "recipes")
@TypeConverters(RecipeTypeConverters::class)
data class RecipeEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val imageUrl: String,
    val cuisine: String,
    val preparationTimeMinutes: Int,
    val cookingTimeMinutes: Int,
    val servings: Int,
    val ingredients: List<RecipeIngredient>,
    val instructions: List<String>,
    val isFavorite: Boolean,
    val cachedAt: Long,
    val hasDetails: Boolean,
)