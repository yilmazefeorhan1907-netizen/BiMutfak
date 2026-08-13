package com.yilmaz.bimutfak.data.remote.api

import com.yilmaz.bimutfak.data.remote.dto.MealResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

// TheMealDB tarif uç noktalarını tanımlar.
interface RecipeApiService {

    // Belirtilen harfle başlayan tarifleri getirir.
    @GET("search.php")
    suspend fun getRecipesByFirstLetter(
        @Query("f") firstLetter: String
    ): MealResponseDto
}