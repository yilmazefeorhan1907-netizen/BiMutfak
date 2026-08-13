package com.yilmaz.bimutfak.data.remote.dto

import com.google.gson.annotations.SerializedName

data class CuisineResponseDto(
    @SerializedName("meals")
    val cuisines: List<CuisineDto>? = null
)

data class CuisineDto(
    @SerializedName("strArea")
    val name: String? = null,

    @SerializedName("strCountry")
    val country: String? = null
)