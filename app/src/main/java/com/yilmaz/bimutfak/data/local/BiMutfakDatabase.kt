package com.yilmaz.bimutfak.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.yilmaz.bimutfak.data.local.converter.RecipeTypeConverters
import com.yilmaz.bimutfak.data.local.dao.ProfileDao
import com.yilmaz.bimutfak.data.local.dao.RecipeDao
import com.yilmaz.bimutfak.data.local.entity.ProfileEntity
import com.yilmaz.bimutfak.data.local.entity.RecipeEntity
import com.yilmaz.bimutfak.data.local.dao.CuisineDao
import com.yilmaz.bimutfak.data.local.entity.CuisineEntity

@Database(
    entities = [
        RecipeEntity::class,
        ProfileEntity::class,
        CuisineEntity::class
    ],
    version = 3,
    exportSchema = false
)
@TypeConverters(RecipeTypeConverters::class)
abstract class BiMutfakDatabase : RoomDatabase() {

    abstract fun recipeDao(): RecipeDao

    abstract fun profileDao(): ProfileDao

    abstract fun cuisineDao(): CuisineDao

    companion object {
        const val DATABASE_NAME = "bimutfak.db"
    }
}