package com.yilmaz.bimutfak.domain.model

// Bir tarifte kullanılan tek bir malzemeyi temsil eder.
data class RecipeIngredient(
    val name: String = "",
    val measure: String = ""
)