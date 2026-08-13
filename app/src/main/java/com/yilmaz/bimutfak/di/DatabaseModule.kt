package com.yilmaz.bimutfak.di

import android.content.Context
import androidx.room.Room
import com.yilmaz.bimutfak.data.local.BiMutfakDatabase
import com.yilmaz.bimutfak.data.local.dao.ProfileDao
import com.yilmaz.bimutfak.data.local.dao.RecipeDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import com.yilmaz.bimutfak.data.local.dao.CuisineDao

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): BiMutfakDatabase {
        return Room.databaseBuilder(
            context,
            BiMutfakDatabase::class.java,
            BiMutfakDatabase.DATABASE_NAME
        )
            .fallbackToDestructiveMigration(true)
            .build()
    }

    @Provides
    fun provideRecipeDao(
        database: BiMutfakDatabase
    ): RecipeDao {
        return database.recipeDao()
    }

    @Provides
    fun provideProfileDao(
        database: BiMutfakDatabase
    ): ProfileDao {
        return database.profileDao()
    }

    @Provides
    fun provideCuisineDao(
        database: BiMutfakDatabase
    ): CuisineDao {
        return database.cuisineDao()
    }
}