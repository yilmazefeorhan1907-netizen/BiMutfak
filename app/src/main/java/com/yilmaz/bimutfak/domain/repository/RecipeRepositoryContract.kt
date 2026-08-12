package com.yilmaz.bimutfak.domain.repository

import com.yilmaz.bimutfak.domain.model.Recipe

interface RecipeRepositoryContract {

    suspend fun getRecipes(): List<Recipe>
}