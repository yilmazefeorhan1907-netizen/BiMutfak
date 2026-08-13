package com.yilmaz.bimutfak.data.remote.api

import com.yilmaz.bimutfak.data.remote.dto.MealResponseDto
import retrofit2.http.GET
import retrofit2.http.Query
import com.yilmaz.bimutfak.data.remote.dto.CuisineResponseDto

// TheMealDB tarif uç noktalarını tanımlar.
interface RecipeApiService {

    @GET("list.php?a=list")
    suspend fun getCuisines(): CuisineResponseDto

    @GET("filter.php")
    suspend fun getRecipesByCuisine(
        @Query("a") cuisine: String
    ): MealResponseDto

    @GET("search.php")
    suspend fun searchRecipes(
        @Query("s") query: String
    ): MealResponseDto

    @GET("lookup.php")
    suspend fun getRecipeById(
        @Query("i") recipeId: String
    ): MealResponseDto
}