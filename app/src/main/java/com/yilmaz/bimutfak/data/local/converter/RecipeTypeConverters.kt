package com.yilmaz.bimutfak.data.local.converter

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.yilmaz.bimutfak.domain.model.RecipeIngredient

class RecipeTypeConverters {

    private val gson = Gson()

    @TypeConverter
    fun fromIngredientList(
        ingredients: List<RecipeIngredient>
    ): String {
        return gson.toJson(ingredients)
    }

    @TypeConverter
    fun toIngredientList(
        value: String
    ): List<RecipeIngredient> {
        val type = object :
            TypeToken<List<RecipeIngredient>>() {}.type

        return gson.fromJson<List<RecipeIngredient>>(
            value,
            type
        ).orEmpty()
    }

    @TypeConverter
    fun fromInstructionList(
        instructions: List<String>
    ): String {
        return gson.toJson(instructions)
    }

    @TypeConverter
    fun toInstructionList(
        value: String
    ): List<String> {
        val type = object :
            TypeToken<List<String>>() {}.type

        return gson.fromJson<List<String>>(
            value,
            type
        ).orEmpty()
    }
}