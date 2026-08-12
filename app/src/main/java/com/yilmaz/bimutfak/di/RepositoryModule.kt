package com.yilmaz.bimutfak.di

import com.yilmaz.bimutfak.data.repository.AuthRepository
import com.yilmaz.bimutfak.data.repository.BasketRepository
import com.yilmaz.bimutfak.data.repository.HouseholdRepository
import com.yilmaz.bimutfak.data.repository.PantryRepository
import com.yilmaz.bimutfak.data.repository.RecipeRepository
import com.yilmaz.bimutfak.data.repository.RecipeSelectionRepository
import com.yilmaz.bimutfak.domain.repository.AuthRepositoryContract
import com.yilmaz.bimutfak.domain.repository.BasketRepositoryContract
import com.yilmaz.bimutfak.domain.repository.HouseholdRepositoryContract
import com.yilmaz.bimutfak.domain.repository.PantryRepositoryContract
import com.yilmaz.bimutfak.domain.repository.RecipeRepositoryContract
import com.yilmaz.bimutfak.domain.repository.RecipeSelectionRepositoryContract
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindRecipeRepository(
        repository: RecipeRepository
    ): RecipeRepositoryContract

    @Binds
    @Singleton
    abstract fun bindRecipeSelectionRepository(
        repository: RecipeSelectionRepository
    ): RecipeSelectionRepositoryContract

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        repository: AuthRepository
    ): AuthRepositoryContract

    @Binds
    @Singleton
    abstract fun bindPantryRepository(
        repository: PantryRepository
    ): PantryRepositoryContract

    @Binds
    @Singleton
    abstract fun bindBasketRepository(
        repository: BasketRepository
    ): BasketRepositoryContract

    @Binds
    @Singleton
    abstract fun bindHouseholdRepository(
        repository: HouseholdRepository
    ): HouseholdRepositoryContract
}