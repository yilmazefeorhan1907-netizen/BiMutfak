package com.yilmaz.bimutfak.data.remote.dto

import com.google.gson.annotations.SerializedName

// TheMealDB tarafından döndürülen tarif listesini temsil eder.
data class MealResponseDto(
    @SerializedName("meals")
    val meals: List<MealDto>? = null
)

// API’den gelen tek bir tarifin temel bilgilerini taşır.
data class MealDto(
    @SerializedName("idMeal")
    val id: String? = null,

    @SerializedName("strMeal")
    val title: String? = null,

    @SerializedName("strMealThumb")
    val imageUrl: String? = null,

    @SerializedName("strInstructions")
    val instructions: String? = null,

    @SerializedName("strCategory")
    val category: String? = null,

    @SerializedName("strArea")
    val area: String? = null
)